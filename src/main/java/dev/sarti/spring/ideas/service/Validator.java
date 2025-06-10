package dev.sarti.spring.ideas.service;

import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Validator<T> extends Function<T, Either<String, T>> {

    default Validator<T> and(Validator<T> other) {
        return value -> this.apply(value).flatMap(other);
    }

    static <T> Validator<T> valid() {
        return value -> Either.right(value);
    }

    static <T> Validator<T> of(Predicate<T> condition, String errorMessage) {
        return value -> condition.test(value)
                ? Either.right(value)
                : Either.left(errorMessage);
    }
}