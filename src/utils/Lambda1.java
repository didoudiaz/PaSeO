package utils;

/**
 *
 * @author diaz
 */
@FunctionalInterface
public interface Lambda1<T, R> { 

    R apply(T t);
}