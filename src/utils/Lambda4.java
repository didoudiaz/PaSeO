package utils;

/**
 *
 * @author diaz
 */
@FunctionalInterface
public interface Lambda4<T, U, V, W, R> {

    R apply(T t, U u, V v, W w);
}