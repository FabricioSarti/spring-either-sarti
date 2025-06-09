package dev.sarti.spring.ideas.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sarti.spring.ideas.configs.exceptions.ValidationException;
import dev.sarti.spring.ideas.domain.Product;
import dev.sarti.spring.ideas.service.EitherUtils;
import dev.sarti.spring.ideas.service.ProductServices;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product productCreated = EitherUtils.orThrow(
                productServices.createProduct(product),
                ValidationException::new // lanza 400
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(productCreated);
    }
}
