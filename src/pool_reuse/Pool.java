package pool_reuse;

/**
 *
 * @author diaz
 * @param <E>
 */
public interface Pool<E> {

    E getTemporary();

    E makePermanent(E elem);

}
