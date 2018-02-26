package solver;

import pool_reuse.VariableValuePool;
import java.util.List;

/**
 *
 * @author diaz
 */
public interface Move {

    void set(Move move);

    void execute(int[] sol);

    List<PairVariableValue> getAssignments(int[] sol, VariableValuePool vvPool);
}
