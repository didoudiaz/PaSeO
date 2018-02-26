package solver;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author diaz
 */
public class TabuListImplMapVV implements TabuList {

    private final Map<PairVariableValue, Integer> map; // key is pair vv

    public TabuListImplMapVV(int size) {
        map = new HashMap<>(size);
    }

    private PairVariableValue makeKey(int variable, int value) {
        System.out.println("TO DO");
        return null;
    }

    @Override
    public void put(int variable, int value, int duration) {
        map.put(makeKey(variable, value), duration);
    }

    @Override
    public void put(PairVariableValue vv, int duration) {
        map.put(vv, duration);
    }

    @Override
    public int get(PairVariableValue vv) {
        Integer duration = map.get(vv);
        return (duration == null) ? Integer.MIN_VALUE : duration;
    }

    @Override
    public int get(int variable, int value) {
        Integer duration = map.get(makeKey(variable, value));
        return (duration == null) ? 0 : duration;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return "tabulist: " + map.toString();
    }
}
