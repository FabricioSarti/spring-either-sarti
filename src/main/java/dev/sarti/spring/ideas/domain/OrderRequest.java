package dev.sarti.spring.ideas.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderRequest {
    private Long customerId, productId;
    private int qty;
}
