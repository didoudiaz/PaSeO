package pool_reuse;

import solver.PairVariableValue;

/**
 *
 * @author diaz
 */
public interface VariableValuePool {

    PairVariableValue getPair(int variable, int value);
}
