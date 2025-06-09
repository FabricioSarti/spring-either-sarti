package dev.sarti.spring.ideas.service;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Either<L, R> {
    public abstract boolean isRight();

    public abstract L getLeft();

    public abstract R getRight();

    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    public <T> Either<L, T> map(Function<R, T> mapper) {
        return isRight() ? right(mapper.apply(getRight())) : left(getLeft());
    }

    public <T> Either<L, T> flatMap(Function<R, Either<L, T>> mapper) {
        return isRight() ? mapper.apply(getRight()) : left(getLeft());
    }

    /**
     * Permite ejecutar efectos colaterales cuando es Right, sin alterar el flujo
     */
    public Either<L, R> peek(Consumer<R> effect) {
        if (isRight())
            effect.accept(getRight());
        return this;
    }

    /** Permite ejecutar efectos colaterales cuando es Left, sin alterar el flujo */
    public Either<L, R> peekLeft(Consumer<L> effect) {
        if (!isRight())
            effect.accept(getLeft());
        return this;
    }

    /** Devuelve un valor predeterminado si es Left */
    public R getOrElse(R defaultValue) {
        return isRight() ? getRight() : defaultValue;
    }

    /** Devuelve un valor predeterminado usando un Supplier si es Left */
    public R getOrElseGet(Supplier<R> supplier) {
        return isRight() ? getRight() : supplier.get();
    }

    /** Convierte Either en un valor unificado */
    public <T> T fold(Function<L, T> leftMapper, Function<R, T> rightMapper) {
        return isRight() ? rightMapper.apply(getRight()) : leftMapper.apply(getLeft());
    }

    /** Convierte Left en una excepción (ideal para controladores) */
    public R orElseThrow(Function<L, ? extends RuntimeException> exceptionMapper) {
        if (isRight())
            return getRight();
        throw exceptionMapper.apply(getLeft());
    }

    private static class Right<L, R> extends Either<L, R> {
        private final R value;

        Right(R value) {
            this.value = value;
        }

        public boolean isRight() {
            return true;
        }

        public L getLeft() {
            throw new NoSuchElementException();
        }

        public R getRight() {
            return value;
        }
    }

    private static class Left<L, R> extends Either<L, R> {
        private final L value;

        Left(L value) {
            this.value = value;
        }

        public boolean isRight() {
            return false;
        }

        public L getLeft() {
            return value;
        }

        public R getRight() {
            throw new NoSuchElementException();
        }
    }
}
