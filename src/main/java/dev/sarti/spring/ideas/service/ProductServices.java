package dev.sarti.spring.ideas.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.sarti.spring.ideas.domain.Product;
import dev.sarti.spring.ideas.domain.ProductValidator;
import dev.sarti.spring.ideas.infrastructure.ProductEntity;
import dev.sarti.spring.ideas.infrastructure.ProductRepo;

@Service
public class ProductServices {
    private final ProductRepo repo;
    private final ProductValidator validator;

    @Autowired
    public ProductServices(ProductRepo repo, ProductValidator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @Transactional
    public Either<String, Product> createProduct(Product p) {
        return validator.validate(p)
                .flatMap(valid -> {
                    ProductEntity e = repo.save(toEntity(valid));
                    return Either.right(toDomain(e));
                });
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
