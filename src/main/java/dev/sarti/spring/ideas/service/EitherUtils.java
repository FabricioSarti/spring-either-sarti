package dev.sarti.spring.ideas.service;

import java.util.function.Function;

public class EitherUtils {
    public static <L, R> R orThrow(Either<L, R> either, Function<L, ? extends RuntimeException> exceptionMapper) {
        if (either.isRight())
            return either.getRight();
        throw exceptionMapper.apply(either.getLeft());
    }

}
