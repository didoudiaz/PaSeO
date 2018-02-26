/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solver;

import java.util.Iterator;

/**
 *
 * @author diaz
 */
public interface Neighborhood extends Iterable<Move> {

    void unsetBestMove();
    
    void setBestMove(Move move);

    Move getBestMove();
    
    @Override
    Iterator<Move> iterator();
}
