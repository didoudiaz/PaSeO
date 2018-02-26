package problem;

import utils.Lambda1;
import utils.Lambda1Throw;
import java.io.File;
import java.io.IOException;
import utils.Lambda0;

/**
 *
 * @author diaz
 */
public enum ProblemCatalog {

    QAP("QAP", "Quadratic Assignment Problem", ProblemArgumentType.FILE_NAME, "QAP file name",
            null, ProblemQAP::new /*(f) -> new ProblemQAP(f)*/, null,
            (p) -> new ModelQAP((ProblemQAP) p));

    public static final ProblemCatalog DEFAULT_PROBLEM = QAP; // put null to force the user to specify a problem

    private final String problemName;
    private final String description;
    private final ProblemArgumentType argumentType;
    private final String argumentName;
    private Object argument;
    private final Lambda0<Problem> ctorProblem; // to create an instance of the problem (no param)
    private final Lambda1Throw<String, Problem> ctorProblemFile; // to create an instance of the problem (param = file name)
    private final Lambda1<Integer, Problem> ctorProblemInt; // to create an instance of the problem (param = integer)
    private final Lambda1<Problem, Model> ctorModel; // to create an instance of a model for this problem (what if several models ?)
    private Problem problemInstance; // singleton

    ProblemCatalog(String problemName, String description, ProblemArgumentType argumentType, String argumentName, Lambda0<Problem> ctorProblem, Lambda1Throw<String, Problem> ctorProblemFile, Lambda1<Integer, Problem> ctorProblemInt, Lambda1<Problem, Model> ctorModel) {
        this.problemName = problemName;
        this.description = description;
        this.argumentType = argumentType;
        this.argumentName = argumentName;
        this.argument = null;
        this.ctorProblem = ctorProblem;
        this.ctorProblemFile = ctorProblemFile;
        this.ctorProblemInt = ctorProblemInt;
        this.problemInstance = null; // singleton
        this.ctorModel = ctorModel;
    }


    /**
     * @return the problemName
     */
    public String getProblemName() {
        return problemName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the argumentName
     */
    public String getArgumentName() {
        return argumentName;
    }

    /**
     * @return the argumentType
     */
    public ProblemArgumentType getArgumentType() {
        return argumentType;
    }

    /**
     * @param argument the argument to set
     */
    public void setArgument(String argument) throws IllegalArgumentException {
        switch (argumentType) {
            case NONE:
                throw new IllegalArgumentException("unexpected argument");

            case FILE_NAME:
                if (!(new File(argument).canRead())) {
                    throw new IllegalArgumentException("Cannot open file " + argument);
                }
                this.argument = argument;
                break;

            case INTEGER:
                try {
                    this.argument = Integer.parseInt(argument);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("invalid integer argument", e);
                }
                break;
        }
    }

    public boolean isArgumentMissing() {
        return argumentType != ProblemArgumentType.NONE && argument == null;
    }

    public Problem createProblemInstance() throws IOException, NumberFormatException {
        if (problemInstance != null) { // singleton
            return problemInstance;
        }

        try {
            switch (argumentType) {
                case NONE:
                    problemInstance = ctorProblem.apply();
                    break;
                case FILE_NAME:
                    problemInstance = ctorProblemFile.apply((String) argument);
                    break;
                case INTEGER:
                    problemInstance = ctorProblemInt.apply((Integer) argument);
                    break;
            }
        } catch (IOException | NumberFormatException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("ERROR creating a Problem ", ex);
        }

        return problemInstance;
    }

    public Model createModelInstance(Problem problem) {
        return ctorModel.apply(problem);
    }

    public static ProblemCatalog getEntry(String name) {
        for (ProblemCatalog entry : values()) {
            if (entry.problemName.equalsIgnoreCase(name)) {
                return entry;
            }
        }
        return null;
    }

    public static void main(String... agrs) throws IOException {
        ProblemCatalog p = QAP;
        p.setArgument(System.getProperty("user.home") + "/QAP/Data/tai10a.qap");
        Model m = p.createModelInstance(p.createProblemInstance());
        System.out.println(m.getSize());
    }
}
