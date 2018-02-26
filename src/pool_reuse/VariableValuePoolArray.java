package pool_reuse;

import java.util.HashMap;
import java.util.Map;
import solver.PairVariableValue;

/**
 *
 * @author diaz
 */
public class VariableValuePoolArray implements VariableValuePool {

    private final Map<Integer, PairVariableValue>[] cachedPairForVariable;

    @SuppressWarnings("unchecked")
    public VariableValuePoolArray(int size, int avgDomainSize) {
        cachedPairForVariable = new Map[size];
        for (int i = 0; i < size; i++) {
            cachedPairForVariable[i] = new HashMap<>(avgDomainSize);
        }
    }

    public PairVariableValue getPair(int variable, int value) {
        PairVariableValue vv = cachedPairForVariable[variable].get(value);
        if (vv == null) {
            vv = new PairVariableValue(variable, value);
            cachedPairForVariable[variable].put(value, vv);
        }
        return vv;
    }
}
