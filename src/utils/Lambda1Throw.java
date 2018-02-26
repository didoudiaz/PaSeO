package utils;

/**
 *
 * @author diaz
 */
@FunctionalInterface
public interface Lambda1Throw<T, R> { 

    R apply(T t) throws Exception;
}