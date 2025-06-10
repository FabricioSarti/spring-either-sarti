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
    private Double price;
    private Integer stock;
    private String category;
    private String warranty;
    private String description;

}
