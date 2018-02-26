package application;

/**
 *
 * @author diaz
 */
import utils.Utils;
import cooperation.CooperativeSolver;
import ctrl_c.CtrlCManager;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import option.OptionTable;
import problem.ProblemCatalog;
import solver.Solver;
import solver.SolverCatalog;

public class Main {

// static String pb = "tai40a";
    static String pb = "dre90";
    static int nbRots = 4;
    static int nbEo = 4;
//    static double force = 0.52; // expon and tai40a
    static double force = 0.53; // expon and dre90
    static String pdf = "exp";
    static int epSize = 16; // set to 0 to disable cooperation
    static int maxIter = 1000000;

// OK with n threads and tai40a
//    public static final int reportInterval = 10000;
//    private final int updateInterval = reportInterval * Utils.randomInterval(10, 25);
// OK with n threads and tai40a
//    public static final int reportInterval = 10000;
//    private final int updateInterval = reportInterval * Utils.randomInterval(10, 50);
// OK with 1 thread and tai40a
//    private final int reportInterval = 50000;
//    private final int updateInterval = reportInterval * Utils.randomInterval(10, 25);
    // OK with n threads and tai40a
//    static int reportInterval = 15000; // tai40a
    static int reportInterval = 50000; // dre90
    static int updateInterval = 250000;

//    static int updateInterval = -1;

//    private final int updateInterval = reportInterval * Utils.randomInterval(10, 25);
//    private final int updateInterval = reportInterval * Utils.randomInterval(10, 50);

    private Main() { // prevent instantiation
    }


    public static void main(String[] args) throws ExecutionException {
        Locale.setDefault(new Locale("en", "US")); // at least for decimal point
        //SolverCatalog x = SolverCatalog.DEFAULT_SOLVER; // use this instead of init() ?
        SolverCatalog.init(); // needed else the class is not initialized (this registers options for solvers)

        CommandLine cmdLine;
        if (args.length == 0) {
            pb = System.getProperty("user.home") + "/QAP/Data/" + pb + ".qap";
            String cmd = "-v 3 -rots " + nbRots + " -eo " + nbEo + " -m " + maxIter + " -ep " + epSize + " -ri " + reportInterval + " " + pb + " -force " + force + " -pdf " + pdf;
            if (updateInterval >= 0) {
                cmd += " -ui " + updateInterval;
            }
            cmdLine = new CommandLine(cmd);
        } else {
            cmdLine = new CommandLine(args);
        }
        ProblemCatalog pcEntry = null;
        try {
            pcEntry = cmdLine.parse();
        } catch (CommandLineException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        if (pcEntry == null) {// stop now -after -h or -v alone: normal stop
            return;
        }

        // record the instances to run in parallel for each solver
        OptionTable.getOptions().stream()
                .filter(o -> o.getAssociatedObject() instanceof SolverCatalog)
                .forEach(o -> {
                    ((SolverCatalog) o.getAssociatedObject()).setCount((int) o.getValue());
                });

        if (SolverCatalog.getNbDifferentSolvers() == 0) {
            SolverCatalog.DEFAULT_SOLVER.setCount(1);
        }

        System.out.println("Solvers: " + SolverCatalog.getNbDifferentSolvers());

        Utils.displayMessage(1, "Main pid: " + Utils.getPID());
        Utils.displayMessage(2, "Parameters (full table)");
        Utils.displayMessage(2, "%s", OptionTable.tableToString());

        CtrlCManager ctrlCMgr = new CtrlCManager();

        Solver bestSolver = null;
        try {
            bestSolver = new CooperativeSolver(ctrlCMgr).solve(pcEntry);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        double timeInSec = Utils.getElapsedTime() / 1000.0;

        // display a newline after a possible output of the string "^C" on terminal
        if (ctrlCMgr.hasBeenInterrupted()) {
            System.out.println("** Interrupted **");
        }

        // should not occur except if there is 0 threads (but prevented in CommandLine using 1 default solver)
        if (bestSolver != null) {
            Utils.displayVector(bestSolver.getBestSolution());
            if (!bestSolver.isTargetReached()) {
                System.out.println("** Target is not reached *** best:");
            }
            System.out.println("solver: " + bestSolver.getSolverNo());
            System.out.println("cost: " + bestSolver.getBestCost());
            System.out.println("iter: " + bestSolver.getBestIteration());
            System.out.printf("time: %.3f sec%n", timeInSec);
            Utils.displayMessage(2, "iters/sec: %d", (int) (bestSolver.getCurrentIteration() / timeInSec));
            statisticsGC();
        }

        ctrlCMgr.stop(); // important to finish with this
    }

    public static void statisticsGC() {
        long countGC = 0;
        long timeGC = 0;

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();
            if (count >= 0) {
                countGC += count;
            }
            long time = gc.getCollectionTime();
            if (time >= 0) {
                timeGC += time;
            }
        }
        Utils.displayMessage(2, "GC: run %d times taking a total of %.3f sec", countGC, timeGC / 1000.0);
    }
}
