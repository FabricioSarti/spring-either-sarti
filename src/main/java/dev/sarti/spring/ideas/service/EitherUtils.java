package dev.sarti.spring.ideas.service;

import java.util.List;
import java.util.Arrays;
import java.util.function.Function;

public class EitherUtils {
    public static <L, R> R orThrow(Either<L, R> either, Function<L, ? extends RuntimeException> exceptionMapper) {
        if (either.isRight())
            return either.getRight();
        throw exceptionMapper.apply(either.getLeft());
    }

    /**
     * Ejecuta los rollback si el Either es Left y luego lanza una excepción.
     */
    public static <L, R> R orThrowWithRollback(
            Either<L, R> either,
            List<Runnable> rollbackActions,
            Function<L, ? extends RuntimeException> exceptionMapper) {

        return either.peekLeft(error -> {

            System.out.println("[ROLLBACK] Error detectado: " + error);
            System.out.println(rollbackActions.size() + " acciones de rollback registradas.");
            rollbackActions.forEach(rollback -> {
                try {
                    rollback.run();
                } catch (Exception e) {
                    System.err.println("[ERROR] Falló una acción de rollback: " + e.getMessage());
                }
            });
        })
                .orElseThrow(exceptionMapper);
    }

    /**
     * Variante que acepta un solo rollback.
     */
    public static <L, R> R orThrowWithRollback(
            Either<L, R> either,
            Runnable rollbackAction,
            Function<L, ? extends RuntimeException> exceptionMapper) {
        return orThrowWithRollback(either, Arrays.asList(rollbackAction), exceptionMapper);

    }

    /**
     * Variante sin rollback, pero con logging explícito.
     */
    public static <L, R> R orThrowWithLog(
            Either<L, R> either,
            Function<L, ? extends RuntimeException> exceptionMapper) {

        return either.peekLeft(error -> System.out.println("[ERROR] Se detectó una falla: " + error))
                .orElseThrow(exceptionMapper);
    }

}
