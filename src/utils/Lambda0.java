package utils;

/**
 *
 * @author diaz
 */
@FunctionalInterface
public interface Lambda0<R> { // same as Supplier

    R apply();
}