/*
 *  QAP Model
 *
 * Some codes are taken from the Taillard's Robust Tabu Search
 * (computeDelta and computeDeltaPart)
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
package problem;

import utils.Utils;
import solver.Move;
import solver.Neighborhood;
import solver.PermutationMove;
import solver.PermutationNeighborhood;

/**
 *
 * @author diaz
 */
public class ModelQAP extends AbstractModel {

    protected final int[][] a;           // flows matrix
    protected final int[][] b;           // distance matrix
    protected final int[][] delta;       // store move costs
    protected final Neighborhood neighborhood; // reuse always the same

    public ModelQAP(ProblemQAP qap) {
        super(qap);
        a = qap.getMatA();
        b = qap.getMatB();
        delta = Utils.allocMatrix(size);
        neighborhood = new PermutationNeighborhood(size);
    }

    /*
     * Compute the cost difference if elements i and j are transposed in
     * permutation (solution) p
     */
    protected int computeDelta(int[] sol, int i, int j) {
        int d = (a[i][i] - a[j][j]) * (b[sol[j]][sol[j]] - b[sol[i]][sol[i]])
                + (a[i][j] - a[j][i]) * (b[sol[j]][sol[i]] - b[sol[i]][sol[j]]);

        for (int k = 0; k < size; k++) {
            if (k != i && k != j) {
                d += (a[k][i] - a[k][j]) * (b[sol[k]][sol[j]] - b[sol[k]][sol[i]])
                        + (a[i][k] - a[j][k]) * (b[sol[j]][sol[k]] - b[sol[i]][sol[k]]);
            }
        }

        return d;
    }

    /*
     * Idem, but the value of delta[i][j] is supposed to be known before the
     * transposition of elements r and s
     *
     */
    protected int computeDeltaPart(int[] sol, int i, int j, int r, int s) {
        return delta[i][j]
                + (a[r][i] - a[r][j] + a[s][j] - a[s][i])
                * (b[sol[s]][sol[i]] - b[sol[s]][sol[j]] + b[sol[r]][sol[j]] - b[sol[r]][sol[i]])
                + (a[i][r] - a[j][r] + a[j][s] - a[i][s])
                * (b[sol[i]][sol[s]] - b[sol[j]][sol[s]] + b[sol[j]][sol[r]] - b[sol[i]][sol[r]]);
    }

    @Override
    public Neighborhood neighborhood() {
        return neighborhood;
    }

    @Override
    public int costOfSolution(int[] sol) {
        int cost = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cost += a[i][j] * b[sol[i]][sol[j]];
            }
        }
        return cost;
    }

    @Override
    public void changeSolution(int cost, int[] sol) {
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                delta[i][j] = computeDelta(sol, i, j);
            }
        }
    }

    @Override
    public int costOfMove(int cost, int[] sol, Move move) {
        int i = ((PermutationMove) move).getVariable1();
        int j = ((PermutationMove) move).getVariable2();
        return cost + delta[i][j];
    }

    @Override
    public int doMove(int cost, int[] sol, Move move) {
        int iRetained = ((PermutationMove) move).getVariable1();
        int jRetained = ((PermutationMove) move).getVariable2();
        /* transpose elements in pos. iRetained and jRetained */
        move.execute(sol);

        /* update solution value */
        cost += delta[iRetained][jRetained];

        /* update matrix of the move costs */
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (i != iRetained && i != jRetained && j != iRetained && j != jRetained) {
                    delta[i][j] = computeDeltaPart(sol, i, j, iRetained, jRetained);
                } else {
                    delta[i][j] = computeDelta(sol, i, j);
                }
            }
        }
        return cost;
    }
}
