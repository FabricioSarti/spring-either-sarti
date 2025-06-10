package dev.sarti.spring.ideas.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sarti.spring.ideas.domain.Product;
import dev.sarti.spring.ideas.infrastructure.CrudPipeline;
import dev.sarti.spring.ideas.infrastructure.ProductEntity;
import dev.sarti.spring.ideas.infrastructure.ProductRepositoryAdapter;

@Service
public class ProductEitherComponentService {

    private final CrudPipeline<ProductEntity, Long> pipeline;

    @Autowired
    public ProductEitherComponentService(ProductRepositoryAdapter repositoryAdapter) {
        this.pipeline = new CrudPipeline<>(repositoryAdapter);
    }

    private static final Validator<ProductEntity> nameValidator = Validator.of(
            p -> p.getName() != null && !p.getName().trim().isEmpty(),
            "El nombre del producto es requerido");

    private static final Validator<ProductEntity> priceValidator = Validator.of(
            p -> p.getPrice() != null && p.getPrice() > 0,
            "El precio debe ser mayor a 0");

    private static final Validator<ProductEntity> stockValidator = Validator.of(
            p -> p.getStock() != null && p.getStock() >= 0,
            "El stock no puede ser negativo");

    // Validador combinado
    private static final Validator<ProductEntity> fullValidator = nameValidator.and(priceValidator).and(stockValidator);

    // Validador que acumula errores
    private static final AccumulatingValidator<ProductEntity> accumulatingValidator = AccumulatingValidator
            .<ProductEntity>of(p -> p.getName() != null && !p.getName().trim().isEmpty(),
                    "El nombre del producto es requerido")
            .and(AccumulatingValidator.of(p -> p.getPrice() != null && p.getPrice() > 0,
                    "El precio debe ser mayor a 0"))
            .and(AccumulatingValidator.of(p -> p.getStock() != null && p.getStock() >= 0,
                    "El stock no puede ser negativo"));

    // Operaciones CRUD
    @Transactional
    public Either<String, ProductEntity> createProduct(Product product) {
        ProductEntity entity = toEntity(product);

        Either<String, ProductEntity> result = pipeline.create(entity, fullValidator);

        // Si la validación falla, forzamos rollback lanzando excepción:
        if (!result.isRight()) {
            throw new IllegalStateException(result.getLeft()); // Spring hará rollback
        }

        return result;
    }

    @Transactional
    public Either<List<String>, ProductEntity> createProductWithAllErrors(Product product) {
        ProductEntity entity = toEntity(product);

        Either<List<String>, ProductEntity> result = pipeline.createWithMultipleValidations(entity,
                accumulatingValidator);

        // Si falla la validación múltiple, forzamos rollback:
        if (!result.isRight()) {
            throw new IllegalStateException("Errores de validación: " + result.getLeft());
        }

        return result;
    }

    public Either<String, ProductEntity> updateProduct(Long id, Function<ProductEntity, ProductEntity> updater) {
        return pipeline.update(id, updater, fullValidator);
    }

    public Either<String, Product> findById(Long id) {
        return pipeline.repository.findById(id)
                .map(this::toDomain) // mapear ProductEntity a Product directamente
                .map(Either::<String, Product>right)
                .getOrElse(Either.left("Producto con id " + id + " no encontrado"));
    }

    public Either<String, List<Product>> findAll() {
        return pipeline.repository.findAll()
                .map(productEntities -> productEntities.stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList()))
                .mapLeft(error -> "No hay productos registrados: " + error);
    }

    public Either<String, Void> deleteById(Long id) {
        return pipeline.repository.deleteById(id);
    }

    // Operación con efectos colaterales
    @Transactional
    public Either<String, Product> createProductWithNotifications(Product product) {
        ProductEntity entity = toEntity(product);

        Either<String, ProductEntity> result = pipeline.createWithEffects(
                entity,
                fullValidator,
                p -> System.out.println("🔄 Creando producto: " + p.getName()),
                p -> System.out.println("✅ Producto creado exitosamente: " + p.getName() + " (ID: " + p.getId() + ")"));

        // Forzar rollback si error:
        if (!result.isRight()) {
            throw new IllegalStateException(result.getLeft());
        }

        return result.map(this::toDomain);
    }

    private ProductEntity toEntity(Product product) {

        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getWarranty(),
                product.getDescription());
    }

    private Product toDomain(ProductEntity productEntity) {
        return new Product(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getStock(),
                productEntity.getCategory(),
                productEntity.getWarranty(),
                productEntity.getDescription());
    }
}
