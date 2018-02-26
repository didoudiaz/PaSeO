package pool_reuse;

import java.util.HashMap;
import java.util.Map;
import utils.Lambda0;
import utils.Lambda0;

/**
 *
 * @author diaz
 * @param <E>
 */
public class PoolImpl<E> implements Pool<E> {

    private final Map<E, E> table;
    private final Lambda0<E> ctor;
    private E currElem;

    public PoolImpl(Lambda0<E> ctor, int initialCapacity) {
        this.table = new HashMap<>(initialCapacity);
        this.currElem = null;
        this.ctor = ctor;
    }

    public PoolImpl(Lambda0<E> ctor) {
        this(ctor, 256);
    }

    @Override
    public E getTemporary() {
        if (currElem == null) {
            currElem = ctor.apply();
        }
        return currElem;
    }

    @Override
    public E makePermanent(E elem) {

        E elemRet = table.putIfAbsent(elem, elem);
        if (elemRet == null) {
            elemRet = elem;
            currElem = null;
        } //else System.out.println(" HAPPENS");
        return elemRet;
    }
}
