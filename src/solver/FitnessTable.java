/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solver;

/**
 *
 * @author diaz
 * @param <I>
 */
public interface FitnessTable<I> {

    void clear();

    I record(int variable, double fitness, I info);

    void sort();

    int getVariable(int rank);

    double getFitness(int rank);

    I getInfo(int rank);
}
