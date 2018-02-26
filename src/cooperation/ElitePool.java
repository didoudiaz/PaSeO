package cooperation;

import utils.Utils;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author diaz
 */
public class ElitePool implements Runnable {

    public static final int MAX_STATES = 100;

    private final int elitePoolSize;
    private final SolverState[] eliteStates;
    private int nbElite;
    private int iMaxElite;

    private final SolverState[] buffStates;
    private int nbBuffAvailable;

    private final BlockingQueue<SolverState> toAdd;

    private final Object blockerLock = new Object();

    public ElitePool(int elitePoolSize, int vecSize) {
        this.elitePoolSize = elitePoolSize;
        eliteStates = new SolverState[elitePoolSize];
        for (int i = 0; i < elitePoolSize; i++) {
            eliteStates[i] = new SolverState(vecSize); // pre-alloc vectors
        }
        nbElite = 0;
        iMaxElite = 0;

        buffStates = new SolverState[MAX_STATES];
        nbBuffAvailable = MAX_STATES;

        for (int i = 0; i < buffStates.length; i++) {
            buffStates[i] = new SolverState(vecSize); // pre-alloc vectors
        }

        toAdd = new ArrayBlockingQueue<>(MAX_STATES);
    }

    @Override
    public void run() {
        if (elitePoolSize == 0) {
            return;
        }
        try {
            for (;;) {
                SolverState s = toAdd.take();
                Utils.displayMessage(4, "EP: add %s", s);
                // add it to pool if relevant
                synchronized (blockerLock) {
                    buffStates[nbBuffAvailable++] = s;
                    insertInPool(s);
                }
            }
        } catch (InterruptedException ex) {
            Utils.displayMessage(4, "EP: INTERRUPTED");
        }
    }

    public boolean isActive() {
        return elitePoolSize > 0;
    }

    private void insertInPool(SolverState s) {
        if (s.compareTo(eliteStates[iMaxElite]) > 0 && nbElite >= elitePoolSize) {
            Utils.displayMessage(5, "EP: NOT INSERTED: greater than max cost %d", eliteStates[iMaxElite].cost);
            return;
        }

        int pos = -1;
        for (int i = 0; i < nbElite; i++) {
//            int cmp = eliteStates[i].compareTo(s);
//            if (cmp == 0 && eliteStates[i].similarSolution(s)) { // same cost and vector: do not insert
            if (eliteStates[i].similarSolution(s)) { // same cost and vector: do not insert (ecept if has a better cost: replace
                if (s.compareTo(eliteStates[i]) >= 0) {
                    Utils.displayMessage(4, "EP: SIMILAR TO pos %d -> NOT INSERTED: ", i);
                    return;
                }
                Utils.displayMessage(2, "EP: SIMILAR TO pos %d but better cost -> REPLACE", i);
                pos = i;
                break;
            }
        }

        if (pos < 0) { // in case not a replace
            pos = (nbElite < elitePoolSize) ? nbElite++ : iMaxElite;
            Utils.displayMessage(2, "EP: ***** INSERT %s%n     at pos %d  old: %d", s, pos, eliteStates[pos].cost);
        }

        eliteStates[pos].assign(s);

        // update iMaxElite
        iMaxElite = 0;
        for (int i = 1; i < nbElite; i++) {
            if (eliteStates[i].compareTo(eliteStates[iMaxElite]) > 0) {
                iMaxElite = i;
            }
        }
        Utils.displayMessage(2, toString());
    }

    public void putSolution(int cost, int[] solution) {
        SolverState s;
        if (elitePoolSize == 0) {
            return;
        }
        synchronized (blockerLock) {
            if (nbBuffAvailable == 0) {
                return;
            }

            s = buffStates[--nbBuffAvailable];
        }
        s.cost = cost;
        Utils.copyVector(solution, s.solution);
        try {
            Utils.displayMessage(4, "EP: PUT " + s);
            toAdd.put(s);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

        }
    }

    public int getSolution(int cost, int[] solution) {
        if (nbElite == 0) {
            return -1;
        }

        int i = Utils.randomInteger(nbElite);
        int c;
        synchronized (blockerLock) {
            c = eliteStates[i].cost;
            if (c > cost) {
                return -1;
            }
            Utils.copyVector(eliteStates[i].solution, solution);
            Utils.displayMessage(4, "EP: GET no %d  cost: %d better than %d", i, c, cost);
        }
        return c;
    }

    private static final String NL = System.getProperty("line.separator");

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("EP: max cost = ").append(eliteStates[iMaxElite].cost);
        if (true) { // to show ordered
            Arrays.sort(eliteStates);
            iMaxElite = nbElite - 1;
        }
        if (elitePoolSize <= 5) {
            s.append(NL);
            for (int i = 0; i < nbElite; i++) {
                s.append("  | ").append(eliteStates[i]).append(NL);
            }
        } else {
            for (int i = 0; i < nbElite; i++) {
                if (i % 10 == 0) {
                    s.append(NL).append("  |");
                }
                s.append("  ").append(eliteStates[i].cost);
            }
            s.append(NL);
        }
        return s.toString();
    }
}
