package dev.sarti.spring.ideas.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sarti.spring.ideas.domain.OrderRequest;
import dev.sarti.spring.ideas.domain.OrderValidator;
import dev.sarti.spring.ideas.infrastructure.OrderEntity;
import dev.sarti.spring.ideas.infrastructure.OrderRepo;
import dev.sarti.spring.ideas.infrastructure.ProductEntity;
import dev.sarti.spring.ideas.infrastructure.ProductRepo;
import dev.sarti.spring.ideas.infrastructure.UserEntity;
import dev.sarti.spring.ideas.infrastructure.UserRepo;

@Service
public class OrderServices {

    private final OrderRepo repo;
    private final OrderValidator validator;
    private final ProductRepo productoRepo;
    private final UserRepo userRepo;
    private final EmailService emailService;

    @Autowired
    public OrderServices(OrderRepo repo, OrderValidator validator, ProductRepo productoRepo, UserRepo userRepo,
            EmailService emailService) {
        this.repo = repo;
        this.validator = validator;
        this.productoRepo = productoRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    @Transactional
    public Either<String, Map<String, String>> placeOrder(OrderRequest req) {
        // Validar la cantidad primero
        return validator.validate(req)
                .flatMap(valid -> findProduct(valid.getProductId())
                        .flatMap(product -> findCustomer(valid.getCustomerId())
                                .flatMap(customer -> checkStock(product, valid.getQty())
                                        .flatMap(updatedProduct -> saveOrder(valid, updatedProduct, customer)))));
    }

    private Either<String, ProductEntity> findProduct(Long id) {
        return productoRepo.findById(id)
                .map(Either::<String, ProductEntity>right)
                .orElseGet(() -> Either.left("Producto no existe"));
    }

    private Either<String, UserEntity> findCustomer(Long id) {
        return userRepo.findById(id)
                .map(Either::<String, UserEntity>right)
                .orElseGet(() -> Either.left("Cliente no existe"));
    }

    private Either<String, ProductEntity> checkStock(ProductEntity product, int qty) {
        if (product.getStock() < qty) {
            emailService.notifyStockShortage(product); // efecto colateral funcional
            return Either.left("No hay stock suficiente");
        }
        product.setStock(product.getStock() - qty);
        productoRepo.save(product);
        return Either.right(product);
    }

    private Either<String, Map<String, String>> saveOrder(OrderRequest req, ProductEntity product, UserEntity user) {
        OrderEntity order = new OrderEntity();
        order.setProductId(product.getId());
        order.setCustomerId(user.getId());
        order.setQuantity(req.getQty());
        order.setCreatedAt(LocalDateTime.now());
        repo.save(order);
        emailService.sendConfirmation(user, order);
        return Either.right(Collections.singletonMap("estado", "Orden confirmada para cliente: " + user.getName()));
    }

}
