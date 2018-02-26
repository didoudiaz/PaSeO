/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

import solver.Move;
import solver.Neighborhood;

/**
 *
 * @author diaz
 */
public interface Model {

    Problem getProblem();

    int getSize();

    Neighborhood neighborhood();

    void initialSolution(int[] sol);

    int costOfSolution(int[] sol);

    void changeSolution(int cost, int[] sol);

    int costOfMove(int cost, int[] sol, Move move);

    int doMove(int cost, int[] sol, Move move);
}
