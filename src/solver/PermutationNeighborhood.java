package solver;

import pool_reuse.Pool;
import pool_reuse.PoolImpl;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author diaz
 */
public class PermutationNeighborhood implements Neighborhood {

    private final int size;
    private final Itr iterator; // reuse always the same iterator to avoid allocations
    private final Pool<PermutationMove> movePool; // used by the iterator (makes sense to declare it here because the model always use the same PermutationNeighborhood)
    private boolean hasBestMove;
    private final PermutationMove bestMove;

    public PermutationNeighborhood(int size) {
        this.size = size;
        iterator = new Itr();
        movePool = new PoolImpl<>(PermutationMove::new, size * size / 2);
        hasBestMove = false;
        bestMove = new PermutationMove();
    }

    @Override
    public Iterator<Move> iterator() {
        iterator.init();
        return iterator;
    }

    @Override
    public void unsetBestMove() {
        hasBestMove = false;
    }

    @Override
    public void setBestMove(Move move) {
        if (move == null) {
            unsetBestMove();
        } else {
            hasBestMove = true;
            bestMove.set(move);
        }
    }

    @Override
    public Move getBestMove() {
        return (hasBestMove) ? bestMove : null;
    }

    private class Itr implements Iterator<Move> {

        int i, j;

        private void init() {
            i = 0;
            j = i + 1;
        }

        @Override
        public boolean hasNext() {
            return i < size - 1; // last move = swap(n-2, n-1), i.e. i = size - 2
        }

        @Override
        public Move next() {
            if (!hasNext())
                throw new NoSuchElementException("Permutation neighborhood of size: " + size);
            PermutationMove move = movePool.getTemporary();
            move.setVariable1(i);
            move.setVariable2(j);
            if (++j >= size) {
                i++;
                j = i + 1;
            }
            move = movePool.makePermanent(move);
            return move;
        }
    }
}
