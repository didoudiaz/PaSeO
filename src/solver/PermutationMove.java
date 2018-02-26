package solver;

import pool_reuse.VariableValuePool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author diaz
 */
public class PermutationMove implements Move {

    private int variable1;
    private int variable2;
    private final List<PairVariableValue> assignements;
    private final List<PairVariableValue> unmodifAssignementsView;

    public PermutationMove() {
        this(0, 0);
    }

    public PermutationMove(int variable1, int variable2) {
        this.variable1 = variable1;
        this.variable2 = variable2;
        this.assignements = new ArrayList<>();
        assignements.add(null); // reserve 2 slots to be able to do .set(index, value)
        assignements.add(null);
        this.unmodifAssignementsView = Collections.unmodifiableList(assignements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermutationMove)) {
            return false;
        }
        PermutationMove move = (PermutationMove) o;
        return variable1 == move.variable1 && variable2 == move.variable2;
    }

    @Override
    public int hashCode() {
        int hash = (variable1 << 16) | (variable2);
        return hash;
    }

    @Override
    public void execute(int[] sol) {
        int tmp = sol[variable1];
        sol[variable1] = sol[variable2];
        sol[variable2] = tmp;
    }

    @Override
    public String toString() {
        return "permutMove(" + variable1 + " <-> " + variable2 + ")";
    }

    /**
     * @return the variable1
     */
    public int getVariable1() {
        return variable1;
    }

    /**
     * @param i the 1st variable to set
     */
    public void setVariable1(int i) {
        this.variable1 = i;
    }

    /**
     * @return the variable2
     */
    public int getVariable2() {
        return variable2;
    }

    /**
     * @param i the 2nd variable to set
     */
    public void setVariable2(int i) {
        this.variable2 = i;
    }

    @Override
    public void set(Move move) {
        this.variable1 = ((PermutationMove) move).variable1;
        this.variable2 = ((PermutationMove) move).variable2;
    }

    @Override
    public List<PairVariableValue> getAssignments(int[] sol, VariableValuePool vvPool) {
        assignements.set(0, vvPool.getPair(variable1, sol[variable2]));
        assignements.set(1, vvPool.getPair(variable2, sol[variable1]));
        return unmodifAssignementsView;
    }
}
