package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author diaz
 * @param <I>
 */
public class FitnessTableImpl<I> implements FitnessTable<I> {

    static final double NO_FITNESS = Double.NEGATIVE_INFINITY;

    final class Fitness {

        int variable;
        double fitness;
        int count; // nb of times this fitness has been reached
        I info; // e.g. a Move associated to this variable

        Fitness(int variable) {
            reset(variable);
        }

        void reset(int variable) {
            this.variable = variable;
            this.fitness = NO_FITNESS;
            this.count = 0;
            this.info = null;
        }

        @Override
        public String toString() {
            return "f(" + variable + "," + fitness + "," + count + "," + info + ")";
        }
    }

    private final int size;
    private final List<Fitness> table;
    private final Comparator<? super Fitness> comparator;

    public FitnessTableImpl(int size) {
        this.size = size;
        table = new ArrayList<>(size);
        for (int variable = 0; variable < size; variable++) {
//            table.set(variable, new Fitness(variable)); // pre-allocate all entries
            table.add(new Fitness(variable)); // pre-allocate all entries
        }

        // descending order since highest fitness are better
        comparator = (x, y) -> (int) Math.signum(y.fitness - x.fitness);
    }

    @Override
    public void clear() {
        for (int variable = 0; variable < size; variable++) {
            Fitness f = table.get(variable);
            f.reset(variable);
        }
    }

    // here we expect that variable corresponds to the rank (this is the case after a clear() and before a sort()
    @Override
    public I record(int variable, double fitness, I info) {
        I old;
        Fitness f = table.get(variable);
//        if (fitness == f.fitness && Utils.randomInteger(++f.count) == 0) {
//            old = f.info;
//            f.info = info;
//        } else {
            if (fitness > f.fitness) { // keep the best (i.e. highest fitness) 
                old = f.info;
                f.fitness = fitness;
                f.count = 0;
                f.info = info;
            } else {
                old = null;
            }
//        }
        return old;
    }

    @Override
    public void sort() {
        table.sort(comparator);
    }

    @Override
    public int getVariable(int rank) {
        return table.get(rank).variable;
    }

    @Override
    public double getFitness(int rank) {
        return table.get(rank).fitness;
    }

    @Override
    public I getInfo(int rank) {
        return table.get(rank).info;
    }

    @Override
    public String toString() {
        return "fitness: " + table.toString();
    }
}
