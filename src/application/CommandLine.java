package application;

import java.io.File;
import option.AmbiguousOptionNameException;
import option.Option;
import option.OptionTable;
import pdf.PDFCatalog;
import problem.ProblemCatalog;
import solver.SolverCatalog;
import static solver.SolverCatalog.ROTS;

/**
 *
 * @author diaz
 */
public class CommandLine {

    public static final File JAR_FILE = getJarFile();
    public static final String JAR_PATH = JAR_FILE.getAbsolutePath();
    public static final String PROGRAM_NAME = "CoopMetaHeur";
    public static final String PROGRAM_DESCRIPTION = "Cooperative Parallel MetaHeuristics";
    public static final String PROGRAM_AUTHOR = "Daniel Diaz";
    public static final String PROGRAM_COPYRIGHT = "Copyright (C) 2016-2017 Daniel Diaz";
    public static final String PROGRAM_VERSION = "0.9";

    private final String[] args;

    public CommandLine(String str) {
        this.args = str.split("  *");
    }

    public CommandLine(String[] args) {
        this.args = args;
    }

    @SuppressWarnings("null")
    public ProblemCatalog parse() throws CommandLineException {
        ProblemCatalog pcEntry = ProblemCatalog.DEFAULT_PROBLEM;
        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-h".equals(arg) || "-help".equals(arg)) {
                showHelp(true);
                return null; // means stop
            }
            if (("-v".equals(arg) && args.length == 1) || "-version".equals(arg)) {
                showHelp(false);
                return null; // means stop
            }
            if (arg.charAt(0) == '-') {
                i = parseOneOption(i);
                continue;
            }
            // non optional arguments = PROBLEM [ FILE | SIZE ]
            ProblemCatalog entry = ProblemCatalog.getEntry(arg);
            if (entry != null) {
                pcEntry = entry;
            } else if (pcEntry == null) {
                throw new CommandLineException("Missing problem name when encountered " + arg);
            } else { // arg is an argument of the problem - check it
                if (!pcEntry.isArgumentMissing()) {
                    throw new CommandLineException("Unexpected argument " + arg);
                }
                try {
                    pcEntry.setArgument(arg);
                } catch (IllegalArgumentException ex) {
                    throw new CommandLineException(ex.getMessage() + " " + arg, ex);
                }
            }
        }

        if (pcEntry == null) {
            throw new CommandLineException("Problem name missing");
        }

        if (pcEntry.isArgumentMissing()) {
            throw new CommandLineException("missing argument " + pcEntry.getProblemName() + " " + pcEntry.getArgumentName());
        }
        return pcEntry;
    }

    @SuppressWarnings("null")
    private int parseOneOption(int index) throws CommandLineException {
        int i = index;
        String opt = args[i];
        String optName = opt.substring(1);
        Option option = null;
        try {
            if (opt.length() == 1 || !Character.isLetter(optName.charAt(0))
                    || (option = OptionTable.getOption(optName)) == null) {
                throw new CommandLineException("Invalid option " + opt);
            }
        } catch (AmbiguousOptionNameException ex) {
            throw new CommandLineException("Ambiguous option " + opt + ": matches: " + ex.getMessage(), ex);
        }

        boolean hasArgument = (i + 1 < args.length && (args[i + 1].charAt(0) != '-'
                || (args[i + 1].length() > 1 && Character.isDigit(args[i + 1].charAt(1)))));

        if (!hasArgument) {
            if (option.isArgumentRequired()) {
                throw new CommandLineException("Argument missing after option " + opt);
            } else {
                option.setValue();
            }
        } else {
            try {
                option.setValue(args[++i]);
            } catch (IllegalArgumentException ex) {
                throw new CommandLineException("Invalid value for option " + opt + " " + args[i] + " (" + ex.getMessage() + ")", ex);
            }
        }

        return i;
    }


    /*
     * Can add this to manifest.mf (which will be integrated in the JAR file)
     * Implementation-Title: Java Cooperative Parallel Metaheuristics 
     * Implementation-Version: 0.9
     * Implementation-Vendor: Daniel Diaz
     * then use 
     * Package p = Main.class.getPackage();
     * String version = p.getImplementationVersion()
     * 
     * pb: when run under the netbeans IDE the JAR file is not used
     * btw: the Jar file can be obtained with 
     * new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()
     * under netbeans IDE this return "classes" (and not a .jar)
     */
    private void showHelp(boolean fullHelp) {
        System.out.println(PROGRAM_DESCRIPTION);
        System.out.println(PROGRAM_NAME + " version " + PROGRAM_VERSION);
        if (!fullHelp) {
            return;
        }
        System.out.println(PROGRAM_COPYRIGHT);
        System.out.println();

        // to run the .class: java -cp /home/diaz/Dropbox/Faculte/ProjetsNetbeans/CoopMetaHeur/build/classes/ application.Main
        System.out.println("Usage: java -jar " + JAR_PATH + " [OPTION]... [PROBLEM] [FILE_NAME|SIZE]");
        System.out.println();
        System.out.println("Options:");
        for (Option p : OptionTable.getOptions()) {
            if (p.getAssociatedObject() == null) {
                showHelpOneOption(p.getShortName(), p.getLongName(), p.getArgumentName(), p.isArgumentRequired(), p.getDescription());
            }
        }
        showHelpOneOption("h", "help", "", true, "show this help and exit");
        showHelpOneOption("v", "version", "", true, "show version number and exit");
        System.out.println();
        System.out.println("Solvers:");
        for (Option p : OptionTable.getOptions()) {
            if (p.getAssociatedObject() instanceof SolverCatalog) {
                showHelpOneOption(p.getShortName(), p.getLongName(), p.getArgumentName(), p.isArgumentRequired(), p.getDescription());
            }
        }
        System.out.println();
        System.out.println("Probability distributions:");
        System.out.print("     ");
        for (PDFCatalog pdfEntry : PDFCatalog.values()) {
            System.out.print(pdfEntry.toString() + " ");
        }
        System.out.println();
        System.out.println();
        System.out.println("Problems:");
        for (ProblemCatalog pcEntry : ProblemCatalog.values()) {
            System.out.printf("     %-5s %s%n", pcEntry.getProblemName() + ":", pcEntry.getDescription());
        }
        System.out.println();
    }

    private void showHelpOneOption(String shortName, String longName, String argumentName, boolean isArgumentRequired, String description) {
        String opt = String.format("-%s, -%s", shortName, longName);
        if (!isArgumentRequired) {
            argumentName = "[" + argumentName + "]";
        }
        System.out.printf("    %-25s %-10s %s%n", opt, argumentName, description);
    }

    public static File getJarFile() {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
