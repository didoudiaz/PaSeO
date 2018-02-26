package utils;

/**
 *
 * @author diaz
 */
@FunctionalInterface
public interface Lambda3<T, U, V, R> {

    R apply(T t, U u, V v);
}