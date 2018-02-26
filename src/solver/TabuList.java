/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solver;

/**
 *
 * @author diaz
 */
public interface TabuList {

    void put(int variable, int value, int duration);

    void put(PairVariableValue vv, int duration);

    int get(int variable, int value);

    int get(PairVariableValue vv);

    void clear();

}
