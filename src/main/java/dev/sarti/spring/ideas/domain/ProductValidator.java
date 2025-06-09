package dev.sarti.spring.ideas.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import dev.sarti.spring.ideas.service.Either;

@Component
public class ProductValidator {
    Rule<Product> nameNotEmpty = Rule.of(
            p -> p.getName() != null && !p.getName().trim().isEmpty(),
            "Nombre obligatorio");

    Rule<Product> pricePositive = Rule.of(
            p -> p.getPrice() > 0,
            "El precio debe ser mayor a cero");

    Rule<Product> stockNonNeg = Rule.of(
            p -> p.getStock() >= 0,
            "El stock no puede ser negativo");

    public Either<String, Product> validate(Product product) {
        List<Rule<Product>> rules = Arrays.asList(nameNotEmpty, pricePositive, stockNonNeg);

        for (Rule<Product> rule : rules) {
            Either<String, Product> result = rule.validate(product);
            if (!result.isRight()) {
                return result;
            }
        }

        return Either.right(product);
    }
}
