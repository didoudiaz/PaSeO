/*
 *  Robust Tabu Search of E. Taillard
 *
 * Main code is taken from the Taillard's Robust Tabu Search
 * original source: mistic.heig-vd.ch/taillard/codes.dir/tabou_pb2.c
 * Original header
 * ***************************************************************
 * Implementation of the robust taboo search of: E. Taillard
 * "Robust taboo search for the quadratic assignment problem",
 * Parallel Computing 17, 1991, 443-455.
 *
 * Copyright : E. Taillard, 1990-2004
 * Standard C version with slight improvement regarding to
 * 1991 version, E. Taillard, 14.03.2006
 * Compatibility: Unix and windows gcc, g++, bcc32.
 * This code can be freely used for non-commercial purpose.
 * Any use of this implementation or a modification of the code
 * must acknowledge the work of E. Taillard
 **************************************************************
 */
package solver;

import utils.Utils;
import cooperation.ElitePool;
import option.OptionTable;
import problem.Model;

/**
 *
 * @author diaz
 */
public class RobustTabuSearch extends AbstractIterativeSearch {

    private final double tabuDurationFactor;      // default 8 * n 
    private final double aspirationFactor;        // default 5 * n * n
    private final int tabuDuration;               // parameter 1 (< n^2/2)
    private final int aspiration;                 // parameter 2 (> n^2/2)

    private final TabuList tabuList;                    // tabu status

    public RobustTabuSearch(Model model, ElitePool elitePool) {
        super(model, elitePool, SolverCatalog.ROTS);

        tabuDurationFactor = (double) OptionTable.TABU_DURATION_FACTOR.getValue();
        tabuDuration = (int) (tabuDurationFactor * size);

        aspirationFactor = (double) OptionTable.ASPIRATION_FACTOR.getValue();
        aspiration = (int) (aspirationFactor * size * size);

        displayMessage(1, "tabu duration : %.2f * %d   = %d", tabuDurationFactor, size, tabuDuration);
        displayMessage(1, "aspiration    : %.2f * %d^2 = %d", aspirationFactor, size, aspiration);

        tabuList = new TabuListImplMap(size);
    }

    private static double cube(double x) {
        return x * x * x;
    }

    @Override
    protected void initializeSolver() {
        tabuList.clear();
    }

    @Override
    protected int doIteration(int currentIteration, int currentCost, int[] sol) {
        int minDelta = Integer.MAX_VALUE;
        boolean alreadyAspired = false;     // in case many moves forced
        Neighborhood neighborhood = model.neighborhood();

        neighborhood.unsetBestMove();

        for (Move move : neighborhood) {
            //System.out.println("the move " + move);
            int costMove = model.costOfMove(currentCost, sol, move);
            int delta = costMove - currentCost;
            boolean aspired = (costMove < bestCost);    // move not tabu?
            boolean autorized = false;                  // move forced?
            for (PairVariableValue vv : move.getAssignments(sol, vvPool)) {
                //System.out.println(" pair " + vv);
                int endTabu = tabuList.get(vv);
                if (endTabu < currentIteration) {
                    autorized = true;
                }

                if (endTabu < currentIteration - aspiration) {
                    aspired = true;
                }
            }
            if ((aspired && !alreadyAspired) // first move aspired
                    || (aspired && alreadyAspired // many move aspired
                    && (delta < minDelta))// => take best one
                    || (!aspired && !alreadyAspired // no move aspired yet
                    && (delta < minDelta) && autorized)) {
                neighborhood.setBestMove(move);
                minDelta = delta;

                if (aspired) {
                    alreadyAspired = true;
                }
            }

        }

        Move bestMove = neighborhood.getBestMove();
        if (bestMove == null) {
            displayMessage(2, "All moves are tabu!");
            tabuList.clear();
        } else {
            /* forbid reverse move for a random number of iterations */
            bestMove.getAssignments(sol, vvPool)
                    .forEach((vv) -> tabuList.put(vv, currentIteration + computeTabuDuration()));
            // NB: it is important to perform doMove AFTER the loop on 
            // bestMove.getAssignments(sol, vvPool) else the obtained assignments 
            // are wrong (and thus wrong couples are put in the tabuList)
            currentCost = model.doMove(currentCost, sol, bestMove);
        }
        return currentCost;
    }

    private int computeTabuDuration() {
        int t;
        do {
            t = (int) (cube(Utils.randomDouble()) * tabuDuration);
        } while (t <= 2);
        return t;

    }

    @Override
    protected int afterAdoptedSolution(int currentCost, int[] sol) {
        tabuList.clear();
        return currentCost;
    }
}
