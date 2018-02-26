package utils;

/**
 *
 * @author diaz
 */
@FunctionalInterface
public interface Lambda2<T, U, R> { // similar to BiFunction

    R apply(T t, U u);
}