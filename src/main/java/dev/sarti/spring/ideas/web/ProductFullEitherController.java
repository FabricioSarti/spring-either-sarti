package dev.sarti.spring.ideas.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sarti.spring.ideas.domain.Product;
import dev.sarti.spring.ideas.service.ProductEitherComponentService;

@RestController
@RequestMapping("/products-full-either")
public class ProductFullEitherController {

    @Autowired
    private ProductEitherComponentService productEitherComponentService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        return productEitherComponentService.createProduct(product)
                .fold(
                        error -> (ResponseEntity<?>) ResponseEntity.badRequest()
                                .body(java.util.Collections.singletonMap("error", error)),
                        success -> (ResponseEntity<?>) ResponseEntity.ok(success));
    }

    @PostMapping("/validate-all")
    public ResponseEntity<?> createProductWithAllValidations(@RequestBody Product product) {
        return productEitherComponentService.createProductWithAllErrors(product)
                .fold(
                        errors -> (ResponseEntity<?>) ResponseEntity.badRequest()
                                .body(java.util.Collections.singletonMap("errors", errors)),
                        success -> ResponseEntity.ok(success));
    }

    @PostMapping("/with-notifications")
    public ResponseEntity<?> createProductWithNotifications(@RequestBody Product product) {
        return productEitherComponentService.createProductWithNotifications(product)
                .fold(
                        error -> (ResponseEntity<?>) ResponseEntity.badRequest()
                                .body(java.util.Collections.singletonMap("error", error)),
                        success -> (ResponseEntity<?>) ResponseEntity.ok(success));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return productEitherComponentService.findById(id)
                .fold(
                        error -> ResponseEntity.notFound().build(),
                        success -> ResponseEntity.ok(success));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return productEitherComponentService.findAll()
                .fold(
                        error -> ResponseEntity.internalServerError()
                                .body(java.util.Collections.singletonMap("error", error)),
                        success -> ResponseEntity.ok(success));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productEitherComponentService.updateProduct(id, existing -> {
            existing.setName(product.getName());
            existing.setPrice(product.getPrice());
            existing.setStock(product.getStock());
            existing.setCategory(product.getCategory());
            existing.setWarranty(product.getWarranty());
            existing.setDescription(product.getDescription());
            return existing;
        }).fold(
                error -> ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", error)),
                success -> ResponseEntity.ok(success));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productEitherComponentService.deleteById(id)
                .fold(
                        error -> ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", error)),
                        success -> ResponseEntity
                                .ok(java.util.Collections.singletonMap("message", "Producto eliminado correctamente")));
    }

}
