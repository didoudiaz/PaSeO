package solver;

/**
 *
 * @author diaz
 */
public class PairVariableValue {

    /**
     * Variable of this pair.
     */
    protected int variable;
    /**
     * Value of this this pair.
     */
    protected int value;

    /**
     * Creates a new pair
     *
     */
    public PairVariableValue() {
    }

    /**
     * Creates a new pair
     *
     * @param variable The variable for this pair
     * @param value The value to use for this pair
     */
    public PairVariableValue(int variable, int value) {
        this.variable = variable;
        this.value = value;
    }

    /**
     * Gets the variable for this pair.
     *
     * @return variable for this pair
     */
    public int getVariable() {
        return variable;
    }

    /**
     * @param variable the variable to set
     */
    public void setVariable(int variable) {
        this.variable = variable;
    }

    /**
     * Gets the value for this pair.
     *
     * @return value for this pair
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * <code>String</code> representation of this pair.</p>
     *
     * @return <code>String</code> representation of this pair
     */
    @Override
    public String toString() {
        return variable + "=" + value;
    }

    /**
     * <p>
     * Generate a hash code for this pair.</p>
     *
     * <p>
     * The hash code is calculated using both the name and the value of the
     * pair.</p>
     *
     * @return hash code for this pair
     */
    @Override
    public int hashCode() {
        return value << 16 | variable;
    }

    /**
     * <p>
     * Test this pair for equality with another <code>Object</code>.</p>
     *
     * <p>
     * Two pairs are considered equal if and only if both the variables and
     * values are equal.</p>
     *
     * @param o the <code>Object</code> to test for equality with this pair
     * @return <code>true</code> if the given <code>Object</code> is equal to
     * this pair else <code>false</code>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PairVariableValue)) {
            return false;
        }
        PairVariableValue pair = (PairVariableValue) o;
        return (variable == pair.variable && value == pair.value);
    }
}
