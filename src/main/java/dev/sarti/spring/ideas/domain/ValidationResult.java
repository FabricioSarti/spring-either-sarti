package dev.sarti.spring.ideas.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.sarti.spring.ideas.service.Either;

public class ValidationResult<T> {
    private final List<String> errors;
    private final T value;

    private ValidationResult(List<String> errors, T value) {
        this.errors = errors;
        this.value = value;
    }

    public static <T> ValidationResult<T> valid(T value) {
        return new ValidationResult<>(Collections.emptyList(), value);
    }

    public static <T> ValidationResult<T> invalid(List<String> errors) {
        return new ValidationResult<>(errors, null);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public T getValue() {
        return value;
    }

    public ValidationResult<T> combine(ValidationResult<T> other) {
        List<String> combinedErrors = new ArrayList<>();
        combinedErrors.addAll(this.errors);
        combinedErrors.addAll(other.errors);

        return combinedErrors.isEmpty()
                ? ValidationResult.valid(this.value != null ? this.value : other.value)
                : ValidationResult.invalid(combinedErrors);
    }

    public Either<List<String>, T> toEither() {
        return isValid() ? Either.right(value) : Either.left(errors);
    }
}
