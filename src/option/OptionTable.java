package option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import solver.SolverCatalog;

/**
 *
 * @author diaz
 */
public class OptionTable {

    public static final Option MAX_ITERATIONS, TARGET_COST;
    public static final Option ELITE_POOL_SIZE, REPORT_INTERVAL, UPDATE_INTERVAL;
    public static final Option TABU_DURATION_FACTOR, ASPIRATION_FACTOR;
    public static final Option PDF_NAME, TAU, FORCE;
    public static final Option /*CHECK_RESULT,*/ VERBOSE_LEVEL;

    private static final List<Option> table;

    private OptionTable() { // prevent instantiation
    }

    static {
        table = new ArrayList<>();

        MAX_ITERATIONS = add("maximal number of iterations", "max-iterations", "mi", OptionArgumentType.INTEGER, 3000000);
        TARGET_COST = add("target cost to reach", "target-cost", "tc", OptionArgumentType.INTEGER, 0);

        ELITE_POOL_SIZE = add("elite pool size", "elite-pool-size", "ep", OptionArgumentType.INTEGER, 0);
        REPORT_INTERVAL = add("report interval", "report-interval", "ri", OptionArgumentType.INTEGER, 10000);
        UPDATE_INTERVAL = add("update interval", "update-interval", "ui", OptionArgumentType.INTEGER, 100000);
        // ROTS
        TABU_DURATION_FACTOR = add("tabu duration factor", "tabu-factor", "tdf", OptionArgumentType.DOUBLE, 8.0);
        ASPIRATION_FACTOR = add("aspiration factor", "aspiration-factor", "af", OptionArgumentType.DOUBLE, 5.0);
        // EO
        PDF_NAME = add("Probability Distribution Function (PDF)", "prob-dist", "pdf", OptionArgumentType.STRING, "power");
        TAU = add("PDF tau parameter", "tau-pdf", "tau", OptionArgumentType.DOUBLE, null);
        FORCE = add("PDF force parameter", "force-pdf", "force", OptionArgumentType.DOUBLE, null);

        //CHECK_RESULT = add("check result", "check", "chk", OptionArgumentType.BOOLEAN, false, true);
        VERBOSE_LEVEL = add("verbose level", "verbose", "v", OptionArgumentType.INTEGER, 1, 2);
    }

    public static Option add(String description, String longName, String shortName, OptionArgumentType argumentType, Object value) {
        return add(description, longName, shortName, argumentType, value, null, null);
    }

    public static Option add(String description, String longName, String shortName, OptionArgumentType argumentType, Object value, Object argumentValueIfNotProvided) {
        return add(description, longName, shortName, argumentType, value, argumentValueIfNotProvided, null);
    }

    public static Option add(String description, String longName, String shortName, OptionArgumentType argumentType, Object value, Object argumentValueIfNotProvided, Object associatedObject) {
        Option option = new Option(description, longName, shortName, argumentType, value, argumentValueIfNotProvided, associatedObject);
        checkNoClash(option);
        table.add(option);
        return option;
    }

    private static void checkNoClash(Option option) {
        table.stream().filter((o) -> (option.isOnErrorWith(o))).forEachOrdered((o) -> {
            throw new IllegalArgumentException("Adding option: " + option.getDescription() + ": conflict between "
                    + option.optionNames() + " and " + o.optionNames());
        });
    }

    public static List<Option> getOptions() {
        return Collections.unmodifiableList(table);
    }

    public static String tableToString() {
        StringBuilder str = new StringBuilder();
        table.forEach((o) -> str.append("   : ").append(o).append(o.isSet() ? "" : " (default)").append("\n"));
        return str.toString();
    }

    @SuppressWarnings("null")
    public static Option getOption(String name) throws AmbiguousOptionNameException {
        Option found = null;
        StringBuilder ambigNames = new StringBuilder();
        for (Option option : table) {
            if (option.isMatchedBy(name)) {
                if (found != null) {
                    ambigNames.append(found.getMatchedName(name)).append(" ");
                }
                found = option;
            }
        }
        if (ambigNames.length() > 0) {
            ambigNames.append(found.getMatchedName(name));
            throw new AmbiguousOptionNameException(ambigNames.toString());
        }

        return found;
    }

    public static void main(String... args) throws AmbiguousOptionNameException {
        SolverCatalog.init();
        Option p = getOption("v");
        p.setValue(123);
        System.out.println(p);
        System.out.println(p.optionNames());
        getOptions().forEach((p1) -> System.out.println(p1));
        System.out.println(p.getValue());
        p.setValue();
        System.out.println(p.getValue());
    }
}
