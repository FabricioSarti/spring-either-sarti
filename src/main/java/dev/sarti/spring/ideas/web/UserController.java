package dev.sarti.spring.ideas.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sarti.spring.ideas.configs.exceptions.NotFoundException;
import dev.sarti.spring.ideas.configs.exceptions.ValidationException;
import dev.sarti.spring.ideas.domain.User;
import dev.sarti.spring.ideas.service.EitherUtils;
import dev.sarti.spring.ideas.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = EitherUtils.orThrow(
                service.create(user),
                ValidationException::new // lanza 400
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        User found = EitherUtils.orThrow(
                service.get(id),
                NotFoundException::new // lanza 404
        );
        return ResponseEntity.ok(found);
    }

}
