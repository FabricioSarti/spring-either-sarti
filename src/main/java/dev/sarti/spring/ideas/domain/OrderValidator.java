package dev.sarti.spring.ideas.domain;

import org.springframework.stereotype.Component;

import dev.sarti.spring.ideas.service.Either;

@Component
public class OrderValidator {
    public Rule<OrderRequest> qtyPositive = Rule.of(
            o -> o.getQty() > 0,
            "La cantidad debe ser mayor a cero");

    public Either<String, OrderRequest> validate(OrderRequest req) {
        return qtyPositive.validate(req);
    }
}
