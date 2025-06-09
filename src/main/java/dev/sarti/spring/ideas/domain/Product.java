package dev.sarti.spring.ideas.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private double price;
    private int stock;

}
