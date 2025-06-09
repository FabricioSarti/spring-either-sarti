package dev.sarti.spring.ideas.service;

import org.springframework.stereotype.Component;

import dev.sarti.spring.ideas.domain.User;
import dev.sarti.spring.ideas.infrastructure.OrderEntity;
import dev.sarti.spring.ideas.infrastructure.ProductEntity;
import dev.sarti.spring.ideas.infrastructure.UserEntity;

@Component
public class EmailService {
    public void sendWelcomeEmail(User user) {
        // Simula envío de correo
        System.out.println("📧 Enviando correo de bienvenida a: " + user.email);
    }

    public void sendConfirmation(UserEntity user, OrderEntity order) {
        System.out.println("📧 Enviando correo a " + user.getEmail() + " por orden " + order.getId());
    }

    public void notifyStockShortage(ProductEntity product) {
        System.out.println("⚠️ Notificación a contabilidad: sin stock de producto " + product.getName());
    }
}
