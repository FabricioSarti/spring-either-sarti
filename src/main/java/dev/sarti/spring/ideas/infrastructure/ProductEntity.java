package dev.sarti.spring.ideas.infrastructure;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class ProductEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private double price;
    private int stock;

}
