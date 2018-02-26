package solver;

import pool_reuse.VariableValuePool;
import pool_reuse.VariableValuePoolImpl;
import utils.Utils;
import cooperation.ElitePool;
import java.util.concurrent.atomic.AtomicInteger;
import option.OptionTable;
import problem.Model;
import problem.Problem;

/**
 *
 * @author diaz
 */
public abstract class AbstractSolver implements Solver {

    protected final int solverNo;
    protected final String solverName;
    protected Thread solverThread;

    protected final Model model;
    protected final Problem problem;

    protected final int size; // problem size;
    protected final int[] bestSolution;              // best solution so far
    protected int bestCost;    // cost of best solution
    protected int bestIteration; // iteration at which the best solution has been reached
    protected final int targetCost;
    protected final ElitePool elitePool;
    protected final VariableValuePool vvPool;

    private static final AtomicInteger solverCount = new AtomicInteger(0);

    public AbstractSolver(Model model, ElitePool elitePool, SolverCatalog scEntry) {
        this.solverNo = solverCount.getAndIncrement();
        this.solverName = scEntry.name();
        this.model = model;
        this.problem = model.getProblem();
        this.size = model.getSize();
        this.bestSolution = Utils.allocVector(size);
        this.bestCost = Integer.MAX_VALUE;
        this.targetCost = (int) OptionTable.TARGET_COST.getValue();
        this.elitePool = elitePool;
        this.vvPool = new VariableValuePoolImpl(size, size / 2);  // what to pass for avgDomainSize ? depends on the problem (anyway it is just an info)
    }

    @Override
    public Solver call() {
        this.solverThread = Thread.currentThread(); // NB: this should not be set in the constructor but here !
        solve();
        return this;
    }

    /**
     * @return the bestSolution
     */
    @Override
    public int[] getBestSolution() {
        return bestSolution;
    }

    /**
     * @return the bestCost
     */
    @Override
    public int getBestCost() {
        return bestCost;
    }

    /**
     * @return the bestIteration
     */
    @Override
    public int getBestIteration() {
        return bestIteration;
    }

    @Override
    public boolean isTargetReached() {
        return bestCost == targetCost;
    }

    public static String formatCostAndGap(int cost, int targetCost) {
        int base = targetCost;
        //int base = cost; // gives wrong APD but used in some papers
        double runTime = Utils.getElapsedTime() / 1000.0;
        String res = "";

        if (base != 0) {
            res = String.format("pd: %6.3f %%  ", 100.0 * (cost - targetCost) / base);
        }

        return String.format("%9d  %stime: %9.3f sec", cost, res, runTime);
    }

    protected final void displayMessage(int level, String format, Object... args) {
        Utils.displayMessage(level, solverNo, solverName, format, args);
    }

    protected boolean isSolverInterrupted() {
        return solverThread.isInterrupted();
    }

    protected String getSolverSystemName() {
        return solverThread.getName();
    }

    /**
     * @return the solverNo
     */
    @Override
    public int getSolverNo() {
        return solverNo;
    }

    /**
     * @return the model
     */
    @Override
    public Model getModel() {
        return model;
    }

    /**
     * @return the problem
     */
    @Override
    public Problem getProblem() {
        return problem;
    }

    /**
     * @return the size
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * @return the targetCost
     */
    @Override
    public int getTargetCost() {
        return targetCost;
    }
}
