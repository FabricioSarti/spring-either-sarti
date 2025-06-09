package dev.sarti.spring.ideas.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sarti.spring.ideas.configs.exceptions.ValidationException;
import dev.sarti.spring.ideas.domain.OrderRequest;
import dev.sarti.spring.ideas.service.EitherUtils;
import dev.sarti.spring.ideas.service.OrderServices;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderServices orderServices;

    @Autowired
    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody OrderRequest orderRequest) {
        Map<String, String> orderCreated = EitherUtils.orThrow(
                orderServices.placeOrder(orderRequest),
                ValidationException::new // lanza 400
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(orderCreated);
    }

}
