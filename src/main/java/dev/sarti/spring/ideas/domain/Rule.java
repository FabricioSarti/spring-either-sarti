package dev.sarti.spring.ideas.domain;

import java.util.function.Predicate;

import dev.sarti.spring.ideas.service.Either;

public class Rule<T> {
    private final Predicate<T> predicate;
    private final String errorMsg;

    public Rule(Predicate<T> predicate, String errorMsg) {
        this.predicate = predicate;
        this.errorMsg = errorMsg;
    }

    public Either<String, T> validate(T value) {
        return predicate.test(value) ? Either.right(value) : Either.left(errorMsg);
    }

    public Rule<T> and(Rule<T> other) {
        return new Rule<>(x -> this.predicate.test(x) && other.predicate.test(x),
                this.errorMsg + " y " + other.errorMsg);
    }

    public static <T> Rule<T> of(Predicate<T> p, String msg) {
        return new Rule<>(p, msg);
    }
}
