package dev.sarti.spring.ideas.service;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import dev.sarti.spring.ideas.domain.ValidationResult;

@FunctionalInterface
public interface AccumulatingValidator<T> extends Function<T, ValidationResult<T>> {

    default AccumulatingValidator<T> and(AccumulatingValidator<T> other) {
        return value -> {
            ValidationResult<T> result1 = this.apply(value);
            ValidationResult<T> result2 = other.apply(value);
            return result1.combine(result2);
        };
    }

    static <T> AccumulatingValidator<T> of(Predicate<T> condition, String errorMessage) {
        return value -> condition.test(value)
                ? ValidationResult.valid(value)
                : ValidationResult.invalid(Collections.singletonList(errorMessage));
    }
}
