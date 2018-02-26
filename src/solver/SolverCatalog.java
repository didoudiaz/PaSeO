package solver;

import utils.Lambda2;
import cooperation.ElitePool;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.stream.Stream;
import option.OptionArgumentType;
import option.OptionTable;
import problem.Model;
import problem.ModelQAP;
import problem.ProblemQAP;

/**
 *
 * @author diaz
 */
public enum SolverCatalog {
    ROTS("Robust Tabu Search", "robust-ts", "rots", RobustTabuSearch::new), // same as lambda: (m, e) -> new RobustTabuSearch(m, e)
    EO("Extremal Optimization", "extremal-opt", "eo", ExtremalOptimization::new);

    public static final SolverCatalog DEFAULT_SOLVER = ROTS;

    private final String description;
    private final String shortName;
    private final String longName;
    private final Lambda2<Model, ElitePool, Solver> ctor;
    private int count; // nb of instances to run in parallel

    SolverCatalog(String description, String longName, String shortName, Lambda2<Model, ElitePool, Solver> ctor) {
        this.description = description;
        this.shortName = shortName;
        this.longName = longName;
        this.ctor = ctor;
        this.count = 0;
//        OptionTable.add("number of " + description, longName, shortName, OptionArgumentType.INTEGER, 0, 0, this);
    }

    public static void init() { // hack: call this method to force the initialization of the class...
        for (SolverCatalog se : values()) {
            OptionTable.add("number of " + se.description, se.longName, se.shortName, OptionArgumentType.INTEGER, 0, 0, se);
        }
    }

    /**
     * @return the description
     */
    public String getDesciption() {
        return description;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the shortName
     */
    public String getLongName() {
        return longName;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    public Solver createSolverInstance(Model model, ElitePool elitePool) {
        return ctor.apply(model, elitePool);
    }

    public static SolverCatalog getEntry(String name) {
        for (SolverCatalog entry : values()) {
            if (entry.shortName.equalsIgnoreCase(name)) {
                return entry;
            }
        }
        return null;
    }

    public static Stream<SolverCatalog> getUsedSolvers() {
        return Arrays.stream(values()).filter(se -> se.count > 0);
    }

    public static int getNbDifferentSolvers() {
        return (int) getUsedSolvers().count();
    }

    public static int getTotalNbSolvers() {
        return (int) getUsedSolvers().mapToInt(s -> s.getCount()).sum();
    }

    public static void main(String... agrs) throws FileNotFoundException {
        SolverCatalog p = EO;
        System.out.println(Arrays.toString(values()));
        Solver s = p.createSolverInstance(new ModelQAP(new ProblemQAP(System.getProperty("user.home") + "/QAP/Data/tai10a.qap")), null);
        System.out.println(s.getSize());
    }
}
