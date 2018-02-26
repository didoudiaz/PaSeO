package solver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author diaz
 */
public class TabuListImplMap implements TabuList {

    private final Map<Integer, Integer> tabuForVariable[];

    @SuppressWarnings("unchecked")
    public TabuListImplMap(int size) {
        tabuForVariable = new Map[size];
        for (int i = 0; i < size; i++) {
            tabuForVariable[i] = new HashMap<>();
        }
    }

    @Override
    public void put(int variable, int value, int duration) {
        tabuForVariable[variable].put(value, duration);
    }

    @Override
    public void put(PairVariableValue vv, int duration) {
        put(vv.getVariable(), vv.getValue(), duration);
    }

    @Override
    public int get(PairVariableValue vv) {
        return get(vv.getVariable(), vv.getValue());
    }

    @Override
    public int get(int variable, int value) {
        Integer duration = tabuForVariable[variable].get(value);
        return (duration == null) ? Integer.MIN_VALUE : duration;
    }

    @Override
    public void clear() {
        for (Map<Integer, Integer> map : tabuForVariable) {
            map.clear();
        }
    }

    @Override
    public String toString() {
        return "tabulist: " + Arrays.toString(tabuForVariable);
    }
}
