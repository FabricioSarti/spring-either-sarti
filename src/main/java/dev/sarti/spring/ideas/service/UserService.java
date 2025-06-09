package dev.sarti.spring.ideas.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.sarti.spring.ideas.domain.User;
import dev.sarti.spring.ideas.domain.UserValidator;
import dev.sarti.spring.ideas.infrastructure.UserEntity;
import dev.sarti.spring.ideas.infrastructure.UserRepo;

@Service
public class UserService {
    private final UserRepo repo;
    private final UserValidator validator;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepo repo, UserValidator validator, EmailService emailService) {
        this.repo = repo;
        this.validator = validator;
        this.emailService = emailService;

    }

    public Either<String, User> create(User u) {
        return validator.validate(u)
                .flatMap(valid -> Either.<String, UserEntity>right(repo.save(toEntity(valid))))
                .map(this::toDomain)
                .peek(user -> emailService.sendWelcomeEmail(user));
    }

    public Either<String, User> get(Long id) {
        return Optional.ofNullable(repo.findById(id))
                .flatMap(Function.identity())
                .map(this::toDomain)
                .map(Either::<String, User>right)
                .orElseGet(() -> Either.left("Usuario con id " + id + " no encontrado"));
    }

    public Either<String, User> update(Long id, User u) {
        return get(id).flatMap(existing -> validator.validate(u)
                .map(valid -> {
                    UserEntity entity = toEntity(valid);
                    entity.setId(id); // nos aseguramos de conservar el ID original
                    UserEntity saved = repo.save(entity);
                    return toDomain(saved);
                }));
    }

    public Either<String, String> delete(Long id) {
        return get(id).map(user -> {
            repo.deleteById(id);
            return "Usuario eliminado correctamente: " + user.name;
        });
    }

    public Either<String, List<User>> list() {
        List<User> users = repo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return users.isEmpty()
                ? Either.left("No hay usuarios registrados.")
                : Either.right(users);
    }

    private UserEntity toEntity(User u) {
        UserEntity entity = new UserEntity();
        entity.setId(u.id);
        entity.setName(u.name);
        entity.setEmail(u.email);
        return entity;
    }

    private User toDomain(UserEntity e) {
        return new User(e.getId(), e.getName(), e.getEmail());
    }

}
