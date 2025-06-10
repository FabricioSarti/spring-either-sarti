package dev.sarti.spring.ideas.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dev.sarti.spring.ideas.service.Either;

@Component
public class ProductRepositoryAdapter implements CrudPipeline.Repository<ProductEntity, Long> {

    @Autowired
    private ProductRepo productRepository;

    @Override
    public Either<String, ProductEntity> save(ProductEntity entity) {
        try {
            ProductEntity saved = productRepository.save(entity);
            return Either.right(saved);
        } catch (Exception e) {
            return Either.left("Error al guardar producto: " + e.getMessage());
        }
    }

    @Override
    public Either<String, ProductEntity> findById(Long id) {
        try {
            return productRepository.findById(id)
                    .map(Either::<String, ProductEntity>right)
                    .orElse(Either.left("Producto no encontrado con ID: " + id));
        } catch (Exception e) {
            return Either.left("Error al buscar producto: " + e.getMessage());
        }
    }

    @Override
    public Either<String, Void> deleteById(Long id) {
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return Either.right(null);
            } else {
                return Either.left("Producto no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            return Either.left("Error al eliminar producto: " + e.getMessage());
        }
    }

    @Override
    public Either<String, List<ProductEntity>> findAll() {
        try {
            List<ProductEntity> products = productRepository.findAll();
            return Either.right(products);
        } catch (Exception e) {
            return Either.left("Error al obtener productos: " + e.getMessage());
        }
    }

}
