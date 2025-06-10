package dev.sarti.spring.ideas.service;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;
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
    public Either<String, Map<String, String>> placeOrder(OrderRequest req, List<Runnable> rollbackActions) {

        return validator.validate(req)
                .flatMap(valid -> findProduct(valid.getProductId())
                        .flatMap(product -> findCustomer(valid.getCustomerId())
                                .flatMap(customer -> checkStock(product, valid.getQty(), rollbackActions,
                                        product.getStock())
                                        .flatMap(updatedProduct -> saveOrder(valid, updatedProduct, customer,
                                                rollbackActions)))));

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

    private Either<String, ProductEntity> checkStock(ProductEntity product, int qty, List<Runnable> rollbackActions,
            int originalStock) {

        // Registrar rollback de stock
        rollbackActions.add(() -> {
            System.out.println("[ROLLBACK] Se restauró el stock a: " + originalStock);
            product.setStock(originalStock);
            productoRepo.save(product);
            System.out.println("[ROLLBACK] Se restauró el stock a: " + originalStock);
        });

        if (product.getStock() < qty) {

            // Usar flatMap para encadenar las operaciones
            /*
             * return emailService.notifyStockShortageRandom(product)
             * .flatMap(notificationSuccess -> {
             * System.out.println("Notificación enviada: " + notificationSuccess);
             * return Either.left("No hay stock suficiente");
             * })
             * .mapLeft(notificationError -> {
             * System.out.println("Error en notificación: " + notificationError);
             * return "Error al enviar notificación de stock: " + notificationError;
             * });
             */

            Either<String, String> notificationResult = emailService.notifyStockShortageRandom(product);

            if (!notificationResult.isRight()) {
                // Si falló la notificación, retornar error de notificación
                String notificationError = notificationResult.getLeft();
                System.out.println("Error en notificación: " + notificationError);
                return Either.left("Error al enviar notificación de stock: " + notificationError);
            } else {
                // Si la notificación fue exitosa, proceder con el error de stock
                String notificationSuccess = notificationResult.getRight();
                System.out.println("Notificación enviada: " + notificationSuccess);
                return Either.left("No hay stock suficiente");
            }
        }

        product.setStock(product.getStock() - qty);
        productoRepo.save(product);

        return Either.right(product);
    }

    private Either<String, Map<String, String>> saveOrder(OrderRequest req, ProductEntity product, UserEntity user,
            List<Runnable> rollbackActions) {
        OrderEntity order = new OrderEntity();
        order.setProductId(product.getId());
        order.setCustomerId(user.getId());
        order.setQuantity(req.getQty());
        order.setCreatedAt(LocalDateTime.now());
        repo.save(order);

        // Registrar rollback de orden
        rollbackActions.add(() -> {
            System.out.println("[ROLLBACK] Intentando eliminar la orden creada con ID: " + order.getId());
            repo.delete(order);
            System.out.println("[ROLLBACK] Se eliminó la orden creada para el cliente: " + user.getName());
        });

        return emailService.sendConfirmationFailure(user, order)
                .flatMap(notificationSuccess -> {
                    System.out.println("Correo enviado: " + notificationSuccess);
                    return Either.right(
                            Collections.singletonMap("estado", "Orden confirmada para cliente: " + user.getName()));
                }).mapLeft(notificationError -> {
                    System.out.println("Error al enviar correo: " + notificationError);
                    // Aquí podrías manejar el error de envío de correo si es necesario
                    return "Error al enviar correo: " + notificationError;
                });
    }

}
