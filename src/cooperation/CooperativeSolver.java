package cooperation;

/**
 *
 * @author diaz
 */
import utils.Utils;
import ctrl_c.CtrlCCallback;
import ctrl_c.CtrlCManager;
import option.OptionTable;
import problem.Problem;
import problem.ProblemCatalog;
import solver.Solver;
import solver.SolverCatalog;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class CooperativeSolver implements CtrlCCallback {

    private final CtrlCManager ctrlCMgr;
    private ExecutorService threadPool = null;

    public CooperativeSolver(CtrlCManager ctrlCMgr) {
        this.ctrlCMgr = ctrlCMgr;
    }

    public Solver solve(ProblemCatalog pcEntry) throws ExecutionException, IOException {
        final Problem problem = pcEntry.createProblemInstance();
        final int size = problem.getSize();

        if (!OptionTable.TARGET_COST.isSet()) {
            OptionTable.TARGET_COST.setValue(problem.getBks());
            if (problem.getBks() > 0) {
                Utils.displayMessage(2, "No target cost given - use BKS: " + problem.getBks());
            }
        }

        final int nbThreads = SolverCatalog.getTotalNbSolvers();

        List<Solver> solvers = new ArrayList<>(nbThreads);
        threadPool = Executors.newFixedThreadPool(nbThreads + 1); // +1 for the elitePool (even if not launched, it is a max number of threads)
        CompletionService<Solver> ecs = new ExecutorCompletionService<>(threadPool);

        ctrlCMgr.registerCallback(this);

        final int elitePoolSize = (int) OptionTable.ELITE_POOL_SIZE.getValue();
        final ElitePool elitePool = new ElitePool(elitePoolSize, size);
        if (elitePool.isActive()) { // ie. elitePoolSize > 0
            threadPool.execute(elitePool);
        }

        displayThreadsInfo(elitePool, nbThreads);

        Solver bestSolver = null;
        try {
            SolverCatalog.getUsedSolvers().forEach(scEntry -> {
                int nb = scEntry.getCount();
                Utils.displayMessage(3, "++ Starting %d instances of %s", nb, scEntry.getShortName());
                for (int i = 0; i < nb; i++) {
                    Solver solver = scEntry.createSolverInstance(pcEntry.createModelInstance(problem), elitePool);
                    solvers.add(solver);
                    ecs.submit(solver);
                }
            });

            Utils.displayMessage(3, "===== Total number of Java threads in the thread group: %d", Thread.activeCount());
            Utils.displayMessage(3, "===== Total number of Java threads: %d", ManagementFactory.getThreadMXBean().getThreadCount());
            for (int i = 0; i < nbThreads; ++i) {
                Solver solver = ecs.take().get();
                Utils.displayMessage(4, "=====** Thread %2d has finished", i);
                if (solver.isTargetReached()) { // stop as soon as one solver reaches a (full) solution
                    bestSolver = solver;
                    break;
                }
            }
            // InterruptedException occurs when threads are interrupted (e.g. in a sleep)
            // RejectedExecutionException occurs when the ThreadPool cannot run new tasks 
            // (e.g. interrupted before all threads could be launched
        } catch (InterruptedException /*| ExecutionException*/ | RejectedExecutionException ex) {
            Utils.displayMessage(5, "===== Threads interrupted " + ex);
        } catch (CancellationException ex) {
            Utils.displayMessage(5, "===== In main thread after SIGINT " + ex);
        }

        // STOP THEM ALL
        threadPool.shutdown(); // very important to correctly terminate the threads

        threadPool.shutdownNow(); // this also is mandatory

        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                Utils.displayMessage(5, "===== Timeout exhausted");
            }
        } catch (InterruptedException ex) {
            Utils.displayMessage(1, "===== Normal interruption of the general cooperative solver");
        }

        if (bestSolver == null) { // nobody reached the targetCost - detect the best one
            bestSolver = findBestSolver(solvers);
        }

        ctrlCMgr.registerCallback(null);
        return bestSolver;
    }

    private void displayThreadsInfo(ElitePool elitePool, int nbThreads) {
        int nbThreadEP = (elitePool.isActive()) ? 1 : 0;

        Utils.displayMessage(1, "===== Available processors: %d", Runtime.getRuntime().availableProcessors());

        Utils.displayMessage(1, "===== Threads for solvers : %d, for Elite Pool: %d%s, for Main: 1, total: %d", nbThreads, nbThreadEP,
                (elitePool.isActive() ? " (Cooperative)" : " (Independent)"), nbThreads + nbThreadEP + 1);

        SolverCatalog.getUsedSolvers()
                .forEach(se -> Utils.displayMessage(2, "%26s: %d", se.getShortName(), se.getCount()));
    }

    private Solver findBestSolver(List<Solver> solvers) {
        Solver bestSolver = null;
        Utils.displayMessage(2, "***** Find Best Solver *****");
        int minCost = Integer.MAX_VALUE;
        for (Solver solver : solvers) {
            if (solver.isTargetReached()) {
                bestSolver = solver;
                break;
            }
            int cost = solver.getBestCost();
            if (cost < minCost) { // record the min
                bestSolver = solver;
                minCost = cost;
            }
        }
        return bestSolver;
    }

    /*
     * This is called when a CTRL+C (SIGINT/SIGTERM) is pressed
     */
    @Override
    public void callback() {
        // shutdown is needed to unblock the take().get() 
        threadPool.shutdown();      // do not accept new taks + send interrupt to all
        threadPool.shutdownNow();   // shutdown immediately
    }
}
