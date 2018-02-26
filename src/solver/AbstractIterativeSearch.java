package solver;

import utils.Utils;
import cooperation.ElitePool;
import option.OptionTable;
import problem.Model;
import static solver.AbstractSolver.formatCostAndGap;

/**
 *
 * @author diaz
 */
public abstract class AbstractIterativeSearch extends AbstractSolver {

    protected final int maxIterations;
    protected final int reportInterval;
    protected final int updateInterval;

    public AbstractIterativeSearch(Model model, ElitePool elitePool, SolverCatalog scEntry) {
        super(model, elitePool, scEntry);
        maxIterations = (int) OptionTable.MAX_ITERATIONS.getValue();
        reportInterval = (int) OptionTable.REPORT_INTERVAL.getValue();
        if (!OptionTable.UPDATE_INTERVAL.isSet()) {
            updateInterval = reportInterval * Utils.randomInterval(10, 50);
        } else {
            updateInterval = (int) OptionTable.UPDATE_INTERVAL.getValue();
        }
        displayMessage(1, "max iterations : %d", maxIterations);
        displayMessage(1, "report interval: %d", reportInterval);
        displayMessage(1, "update interval: %d", updateInterval);
    }

    private int[] currentSolution;                  // current solution
    private int currentIteration;       // current iteration
    private int currentCost;            // current sol. value

    /**
     * @return the currentSolution
     */
    @Override
    public int[] getCurrentSolution() {
        return currentSolution;
    }

    /**
     * @return the currentIteration
     */
    @Override
    public int getCurrentIteration() {
        return currentIteration;
    }

    /**
     * @return the currentCost
     */
    @Override
    public int getCurrentCost() {
        return currentCost;
    }
    
    @Override
    public void solve() {
        boolean bestSent;

        currentSolution = Utils.allocVector(size);

        initializeSolver();

        model.initialSolution(currentSolution);
        currentCost = model.costOfSolution(currentSolution);
        model.changeSolution(currentCost, currentSolution);

        Utils.copyVector(currentSolution, bestSolution);
        bestCost = currentCost;
        bestIteration = 0;
        bestSent = false;

        for (currentIteration = 1; currentIteration <= maxIterations && bestCost > targetCost && !isSolverInterrupted(); currentIteration++) {

            /* report and update actions */
            if (elitePool.isActive()) { // this test is not mandatory (only avoids false trace display REPORT & PICK if there is no elitePool)
                if (!bestSent && currentIteration - bestIteration > 500) {
                    displayMessage(2, "REPORT BEST  cost: %d (report interval: %d iters)", bestCost, reportInterval);
                    elitePool.putSolution(bestCost, bestSolution);
                    bestSent = true;
                } else if (currentIteration % reportInterval == solverNo) {
                    displayMessage(4, "REPORT CURR  cost: %d (report interval: %d iters)", currentCost, reportInterval);
                    elitePool.putSolution(currentCost, currentSolution);
                }

                if (currentIteration % updateInterval == solverNo) {
                    if (!bestSent) {
                        elitePool.putSolution(bestCost, bestSolution);
                        bestSent = true;
                    }
                    int c = elitePool.getSolution(currentCost, currentSolution); // return negative cost if not changed
                    if (c >= 0) {
                        displayMessage(2, "PICK  cost: %d  better than %d (update interval: %d iters)", c, currentCost, updateInterval);
                        currentCost = c;
                        if (currentCost < bestCost) {
                            bestCost = currentCost;
                            bestIteration = currentIteration;
                            Utils.copyVector(currentSolution, bestSolution);
                            bestSent = true;// dont report it to elitePool since it comes from elitePool !
                        }
                        currentCost = afterAdoptedSolution(currentCost, currentSolution);
                        model.changeSolution(currentCost, currentSolution);
                    }
                }
            }
            currentCost = doIteration(currentIteration, currentCost, currentSolution);

            /* best solution improved ? */
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestIteration = currentIteration;
                Utils.copyVector(currentSolution, bestSolution);
                bestSent = false;
                displayMessage(1, "iter:%9d  cost: %s", currentIteration, formatCostAndGap(bestCost, targetCost));
                Utils.displayVector(3, bestSolution);
            }
        }

        if (isSolverInterrupted()) {
            displayMessage(4, "INTERRUPTED");
        }
    }

    protected void initializeSolver() {
    }

    protected abstract int doIteration(int currentIteration, int currentCost, int[] sol);

    protected int afterAdoptedSolution(int currentCost, int[] sol) {
        return currentCost;
    }
}
