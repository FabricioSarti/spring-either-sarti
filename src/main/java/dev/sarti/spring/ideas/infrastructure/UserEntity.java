package dev.sarti.spring.ideas.infrastructure;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name, email;

}
