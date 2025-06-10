package dev.sarti.spring.ideas.infrastructure;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import dev.sarti.spring.ideas.service.AccumulatingValidator;
import dev.sarti.spring.ideas.service.Either;
import dev.sarti.spring.ideas.service.Validator;

@Component
public class CrudPipeline<T, ID> {

    /**
     * A simple CRUD pipeline that uses a repository to perform create, read,
     * update, and delete operations.
     * It supports validation and effects before and after saving an entity.
     *
     * @param <T>  the type of the entity
     * @param <ID> the type of the entity's identifier
     */

    public interface Repository<T, ID> {
        public Either<String, T> save(T entity);

        public Either<String, T> findById(ID id);

        public Either<String, Void> deleteById(ID id);

        public Either<String, List<T>> findAll();
    }

    public final Repository<T, ID> repository;

    public CrudPipeline(Repository<T, ID> repository) {
        this.repository = repository;
    }

    public Either<String, T> create(T entity, Validator<T> validator) {
        return validator.apply(entity)
                .flatMap(repository::save);
    }

    public Either<List<String>, T> createWithMultipleValidations(T entity, AccumulatingValidator<T> validator) {
        return validator.apply(entity)
                .toEither()
                .flatMap(validEntity -> repository.save(validEntity)
                        .mapLeft(error -> java.util.Collections.singletonList(error)));
    }

    public Either<String, T> update(ID id, Function<T, T> updater, Validator<T> validator) {
        return repository.findById(id)
                .map(updater)
                .flatMap(validator)
                .flatMap(repository::save);
    }

    public Either<String, T> createWithEffects(T entity,
            Validator<T> validator,
            Consumer<T> beforeSave,
            Consumer<T> afterSave) {
        return validator.apply(entity)
                .peek(beforeSave)
                .flatMap(repository::save)
                .peek(afterSave);
    }
}