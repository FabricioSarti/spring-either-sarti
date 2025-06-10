package dev.sarti.spring.ideas.service;

import java.util.Random;

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

    // Opción 2: Simular con probabilidad aleatoria
    public Either<String, String> notifyStockShortageRandom(ProductEntity product) {
        Random random = new Random();

        // 30% de probabilidad de fallo
        if (random.nextDouble() < 0.3) {
            String[] errors = {
                    "Error de conexión con el servicio de notificaciones",
                    "Timeout al enviar notificación",
                    "Servicio de email no disponible",
                    "Error de autenticación"
            };
            String error = errors[random.nextInt(errors.length)];
            return Either.left(error);
        }

        // Caso exitoso
        System.out.println("⚠️ Notificación a contabilidad: sin stock de producto " + product.getName());
        return Either.right("Notificación enviada exitosamente");
    }

    // Opción 2: Simular con probabilidad aleatoria
    public Either<String, String> sendConfirmationFailure(UserEntity user, OrderEntity order) {
        Random random = new Random();

        // 90% de probabilidad de fallo
        if (random.nextDouble() < 0.9) {
            String[] errors = {
                    "Error de conexión con el servicio de notificaciones",
                    "Timeout al enviar notificación",
                    "Servicio de email no disponible",
                    "Error de autenticación"
            };
            String error = errors[random.nextInt(errors.length)];
            return Either.left(error);
        }

        // Caso exitoso
        return Either.right(" Enviando correo a " + user.getEmail() + " por orden " + order.getId());
    }
}
