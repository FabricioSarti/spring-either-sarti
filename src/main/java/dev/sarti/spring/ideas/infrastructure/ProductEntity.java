package dev.sarti.spring.ideas.infrastructure;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String category;
    private String warranty;
    private String description;

}
