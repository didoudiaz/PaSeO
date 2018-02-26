/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cooperation;

import utils.Utils;
import java.util.Arrays;

/**
 *
 * @author diaz
 */
public class SolverState implements Comparable<SolverState> {

    int cost;
    int[] solution;

    public SolverState(int size) {
        this.cost = Integer.MAX_VALUE;
        this.solution = Utils.allocVector(size);
    }

    public SolverState(int cost, int[] solution) {
        this.cost = cost;
        this.solution = Arrays.copyOf(solution, solution.length);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("state: ").append(cost).append(" =");
        for (int i : solution) {
            s.append(" ").append(i);
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof SolverState)) {
            return false;
        }
        SolverState s = (SolverState) o;
        return cost == s.cost;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + this.cost;
        hash = 13 * hash + Arrays.hashCode(this.solution);
        return hash;
    }

    @Override
    public int compareTo(SolverState s) {
        return cost - s.cost;
    }

    public void assign(SolverState s) {
        this.cost = s.cost;
        Utils.copyVector(s.solution, solution);
    }

    public boolean similarSolution(SolverState s) {
        int nbDiff = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] != s.solution[i] && ++nbDiff > 4) {
                return false;
            }
        }

        return true;
    }
}
