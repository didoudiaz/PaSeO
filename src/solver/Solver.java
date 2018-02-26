/*
 */
package solver;

import java.util.concurrent.Callable;
import problem.Model;
import problem.Problem;

/**
 *
 * @author diaz
 */
public interface Solver extends Callable<Solver> {

    void solve();

    int[] getCurrentSolution();

    int getCurrentCost();

    int getCurrentIteration();

    int[] getBestSolution();

    int getBestCost();

    int getBestIteration();

    boolean isTargetReached();

    int getSolverNo();

    Model getModel();

    Problem getProblem();

    int getSize();

    int getTargetCost();
}
