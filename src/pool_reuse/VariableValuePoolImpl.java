package pool_reuse;

import solver.PairVariableValue;

/**
 *
 * @author diaz
 */
public class VariableValuePoolImpl implements VariableValuePool {

    private final PoolImpl<PairVariableValue> vvPool;

    public VariableValuePoolImpl(int size, int avgDomainSize) {
        vvPool = new PoolImpl<>(PairVariableValue::new, size * avgDomainSize); // what initialCapacity to pass ?
    }

    @Override
    public PairVariableValue getPair(int variable, int value) {
        PairVariableValue vv = vvPool.getTemporary();
        vv.setVariable(variable);
        vv.setValue(value);
        return vvPool.makePermanent(vv);
    }
}
