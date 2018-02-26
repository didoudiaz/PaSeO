/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

/**
 *
 * @author diaz
 */
public interface Problem {

    /**
     * @return the Best Known Solution (best known cost)
     */
    int getBks();

    /**
     * @return the lower bound
     */
    int getBound();

    /**
     * @return the opt
     */
    int getOptimum();

    /**
     * @return the size
     */
    int getSize();

}
