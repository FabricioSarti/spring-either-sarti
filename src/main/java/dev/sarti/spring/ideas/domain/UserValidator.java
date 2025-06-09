package dev.sarti.spring.ideas.domain;

import org.springframework.stereotype.Component;

import dev.sarti.spring.ideas.service.Either;

@Component
public class UserValidator {
    private final Rule<String> notNull = Rule.of(s -> s != null && !s.trim().isEmpty(), "Campo obligatorio");
    private final Rule<String> emailFormat = Rule.of(e -> e.contains("@") && e.contains("."), "Email inválido");
    private final Rule<String> nameLength = Rule.of(n -> n.length() >= 2 && n.length() <= 20, "Nombre inválido");

    public Either<String, User> validate(User user) {
        return notNull
                .and(nameLength)
                .validate(user.name)
                .flatMap(n -> notNull.and(emailFormat)
                        .validate(user.email))
                .map(valid -> user);
    }
}
