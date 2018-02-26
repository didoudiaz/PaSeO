package problem;

/**
 *
 * @author diaz
 */
public abstract class AbstractProblem implements Problem {

    /**
     * size of the problem (always known)
     */
    protected int size;
    /**
     * optimal cost (0 if unknown)
     */
    protected int opt;
    /**
     * best bound (0 if unknown)
     */
    protected int bound;
    /**
     * best known solution cost (0 if unknown)
     */
    protected int bks;

    /**
     * @return the size
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * @return the opt
     */
    @Override
    public int getOptimum() {
        return opt;
    }

    /**
     * @return the bound
     */
    @Override
    public int getBound() {
        return bound;
    }

    /**
     * @return the bks
     */
    @Override
    public int getBks() {
        return bks;
    }

}
