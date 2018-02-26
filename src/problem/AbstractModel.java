/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

import utils.Utils;

/**
 *
 * @author diaz
 */
public abstract class AbstractModel implements Model {

    protected Problem problem;
    protected int size;

    public AbstractModel(Problem problem) {
        this.problem = problem;
        this.size = problem.getSize();
    }

    @Override
    public Problem getProblem() {
        return problem;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void initialSolution(int[] sol) {
        Utils.randomPermut(sol);
    }

}
