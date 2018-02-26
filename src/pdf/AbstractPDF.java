package pdf;

import utils.Utils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import option.OptionTable;

/**
 *
 * @author diaz
 */
public abstract class AbstractPDF implements PDF {

    protected static final double EPSILON_FOR_FORCE = 1e-10;

    public static final Double UNSET = null;

    public static boolean isUnset(Double x) {
        return x == null;
    }

    public static boolean isSet(Double x) {
        return x != null;
    }


    /* name of the PDF */
    private final String pdfName;

    /* monotonocity type */
    private final Monotonicity monot;

    /* #values needed (from 1..size), i.e. size of the problem */
    private final int size;

    /* tau parameter */
    private double tau;

    /* force level */
    private double force;

    /* default tau */
    private final double defaultTau;

    /* force: tau min (to compute tau from force when force is given) */
    protected final double forceTauInf;

    /* force: tau sup */
    protected final double forceTauSup;

    /* array of tabled PDF values (with size + 1 entries since indexes from 1..size) */
    protected final double[] pdfValue;

    protected final double forceXMin; // interval to compute tau from force
    protected final double forceXMax;

    protected AbstractPDF(String pdfName, Monotonicity monot, int size, Double tauOptional, Double forceOptional,
            double defaultTau, double forceTauInf, double forceTauSup) {
        this.pdfName = pdfName;
        this.monot = monot;
        this.size = size;
        this.forceXMin = 1;
        this.forceXMax = size * 0.2;
        this.defaultTau = defaultTau;
        this.forceTauInf = forceTauInf;
        this.forceTauSup = forceTauSup;
        this.pdfValue = new double[size + 1]; // start from 1
        initialize(tauOptional, forceOptional);
    }

    private void initialize(Double tauOptional, Double forceOptional) {
        if (isUnset(forceOptional)) {
            tau = isUnset(tauOptional) ? defaultTau : tauOptional;
        } else if (isSet(tauOptional)) {
            System.out.println("Warning: both tau and force are given, force is ignored");
            tau = tauOptional;
            forceOptional = UNSET;
        } else {
            force = forceOptional;  // force given : compute tau from force
            if (monot != Monotonicity.NON_MONOTONE) {
                tau = computeTauFromForceMonotone(force);
            } else {
                tau = computeTauFromForceNonMonot(force);
            }
        }
        double sum = 0;
        for (int x = 1; x <= size; x++) {
            double y = pdf(x, tau);
            pdfValue[x] = y;
            sum += y;
        }

        /* Normalization process to ensure it is a PDF (i.e. the sum = 1) happens very often, 
         * e.g. for semi-PDF (e.g. our normal only uses the right-half part of the normal law)
         */
        if (sum != 1.0) {
            Utils.displayMessage(2, "Normalizing all value because sum = %g", sum);
            for (int x = 1; x <= size; x++) {
                pdfValue[x] /= sum;
            }
        }

        if (isUnset(forceOptional)) {
            force = computeForce(tau);
        }
    }

    protected abstract double pdf(int x, double tau);

    /*
     *  Computes tau from force in the case the PDF force is monotone
     *
     *  The PDF force is monotone if it "evolves" always in the same "direction"
     *  when tau increases. For instance in the power law, increasing tau always increases
     *  the force, i.e. gives "more probabilities" to first x1, x2,... 
     *
     *  More formally, a PDF force is said monotone if either
     *     For all tau1 > tau2 and for all x=1,...,size  PDF(x,tau1) >= PDF(x,tau2) 
     *  or For all tau1 > tau2 and for all x=1,...,size  PDF(x,tau1) <= PDF(x,tau2) 
     *  Else it is said non-monotone
     *
     *  For the monotone case we use a binary search.
     */
    private double computeTauFromForceMonotone(double force) {
        double tauInf = forceTauInf;
        double tauSup = forceTauSup;
        double xMin = forceXMin;
        double xMax = forceXMax;
        int forceX = (int) (xMax - force * (xMax - xMin));
        int x;
        double sum1;
        double tau1;

        if (isUnset(tauSup)) {
            tauSup = size * size;
        }

        if (forceX > size) {
            forceX = size;
        }

        Utils.displayMessage(3, "Force X in [%g:%g] lineary with probability %g => X = %d", xMin, xMax, force, forceX);
        Utils.displayMessage(2, "Find tau s.t. X in 1..%d represents %g of the PDF (%s)", forceX, force, monot);

        do {
            tau1 = (tauInf + tauSup) / 2;
            double sum = 0, y;
            for (x = 1; x <= size; x++) {
                y = pdf(x, tau1);
                pdfValue[x] = y;
                sum += y;
            }
            sum1 = 0;
            for (x = 1; x <= forceX; x++) {
                y = pdfValue[x] / sum;
                sum1 += y;
                if (sum1 > force) {
                    break;
                }
            }

            Utils.displayMessage(4, "tau inf:%.12f sup:%.12f mid:%.12f  Sum = %.12f  x = %d    |sum-force|: %.12f  sup-inf: %.12f",
                    tauInf, tauSup, tau1, sum1, x, Math.abs(sum1 - force), tauSup - tauInf);

            if ((monot == Monotonicity.FORCE_GROWS_AS_TAU && sum1 > force) || (monot == Monotonicity.FORCE_GROWS_AS_INV_TAU && sum1 < force)) {
                tauSup = tau1;
            } else {
                tauInf = tau1;
            }
        } while (Math.abs(sum1 - force) > EPSILON_FOR_FORCE && tauSup - tauInf > EPSILON_FOR_FORCE);

        Utils.displayMessage(2, "Force %g finished: sum probabilities in 1..%d = %g  ==>  tau: %g", force, forceX, sum1, tau1);

        return tau1;
    }

    /*
     *  Computes tau from force in the case the PDF is non-monotone
     *
     *  (see above for definition)
     *
     *  We use a heuristics by splitting the range for tau and trying to 
     *  improve the best found
     */
    private double computeTauFromForceNonMonot(double force) {
        double tauInf = forceTauInf;
        double tauSup = forceTauSup;
        double xMin = forceXMin;
        double xMax = forceXMax;
        int forceX = (int) (xMax - force * (xMax - xMin));

        if (forceX > size) {
            forceX = size;
        }

        Utils.displayMessage(3, "Force X in [%g:%g] lineary with probability %g => X = %d", xMin, xMax, force, forceX);
        Utils.displayMessage(2, "Find tau s.t. X in 1..%d represents %g of the PDF (%s)", forceX, force, monot);

        double nbSamples = 16;
        int tries = 1000;
        double bestTau = 0;
        double bestDist = Double.POSITIVE_INFINITY;
        double bestSum = 0;

        for (;;) {
            Utils.displayMessage(4, "BETWEEN %g .. %g (nr samples: %g)", tauInf, tauSup, nbSamples);
            double step = (tauSup - tauInf) / nbSamples;
            for (double currTau = tauInf; currTau <= tauSup; currTau += step) {
                double sum = 0, y;
                for (int x = 1; x <= size; x++) {
                    y = pdf(x, currTau);
                    pdfValue[x] = y;
                    sum += y;
                }
                double sum1 = 0;
                for (int x = 1; x <= forceX; x++) {
                    y = pdfValue[x] / sum;
                    sum1 += y;
                }

                double dist = Math.abs(sum1 - force);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestTau = currTau;
                    bestSum = sum1;
                }
            }

            //if (--tries == 0)
            if (bestDist < EPSILON_FOR_FORCE || --tries == 0) {
                break;
            }

            Utils.displayMessage(4, "BEST TAU: %g", bestTau);
            double t = bestTau - step;
            if (t > tauInf) {
                tauInf = t;
            }

            t = bestTau + step;
            if (t < tauSup) {
                tauSup = t;
            }
            nbSamples = (nbSamples < 256) ? nbSamples * 2 : nbSamples * 1.2;

            if (tauSup - tauInf < EPSILON_FOR_FORCE) {
                break;
            }
        }

        Utils.displayMessage(2, "Force %g finished: sum probabilities in 1..%d = %g  ==>  tau: %g", force, forceX, bestSum, bestTau);
        return bestTau;
    }

    /*
     *  Computes the force level of the current PDF+tau
     */
    private double computeForce(double tau) {
        double xMin = forceXMin;
        double xMax = forceXMax;
        double sum = 0;
        double bestDist = Double.POSITIVE_INFINITY;
        int bestForceX = 0;
        double bestForce = 0;

        Utils.displayMessage(2, "Find force corresponding to tau = %g", tau);

        for (int x = 1; x <= xMax; x++) {
            sum += pdfValue[x];
            if (x < xMin) {
                continue;
            }

            /* compare sum to related force */
            double currForce = (xMax - x) / (xMax - xMin);
            double dist = Math.abs(currForce - sum);
            Utils.displayMessage(4, "forceX: %d  force: %g  sum: %g  dist: %g", x, currForce, sum, dist);
            if (dist < bestDist) {
                bestDist = dist;
                bestForceX = x;
                bestForce = sum;
            }
        }

        Utils.displayMessage(2, "Found: best force level = %g (i.e. X in 1..%d represents %g of the PDF)", bestForce, bestForceX, bestForce);
        return bestForce;
    }


    /*
     *  Emits gnuplot data files
     */
    @Override
    @SuppressWarnings("null")
    public String generateGnuPlotFiles(String gplotPrefix, boolean histogram) throws FileNotFoundException {
        String gplotFile = gplotPrefix + ".gplot";
        String dataFile = gplotPrefix + ".dat";
        String resFile = gplotPrefix + ".pdf";
        PrintWriter out = null;
        try {
            out = new PrintWriter(dataFile, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex); // cannot happen
        }
        out.printf("# PDF: %s  size: %d  tau: %g  force: %g%n", pdfName, size, tau, force);

        for (int x = 1; x <= size; x++) {
            out.printf("%3d %f%n", x, pdfValue[x]);
        }
        out.close();
        try {
            out = new PrintWriter(gplotFile, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex); // cannot happen
        }

        int t = size / 10;
        if (t >= 10) {
            t = 10;
        } else if (t >= 5) {
            t = 5;
        } else {
            t = 2;
        }

        out.println("set terminal pdf");
        out.println();
        out.println("size=" + size);
        out.println();
        out.println("set xrange [1:size]");
        out.printf("set xtics %d,%d%n", t, t);
        out.println("set xtics add (1)");
        out.println("#set xtics add (size)");
        out.println();
        out.println("set samples size");
        out.println();
        out.printf("set output \"%s\"%n", resFile);
        //  out.printf(set title \"PDF: %s   size: %d   tau: %g   force: %g\"%n", pdfName, size, tau, force);
        out.printf("set title \"Law: %s   size: %d   force: %g\"%n", pdfName, size, force);
        if (!histogram) {
            out.printf("plot \"%s\" with lines title \"tau = %g\"%n", dataFile, tau);
        } else {
            out.println("set xrange [0:size]");
            out.println("set style fill solid");
            out.println("set boxwidth 0.5");
            out.printf("plot \"%s\" with boxes title \"probability distribution\"%n", dataFile);
        }
        out.close();
        /*  
         * on MacOSX maybe gnuplot is in /opt/local/bin...
         * PATH shoud be correctly set (but correct global PATH setting is tricky in MacOS).
         */
        String cmd = "gnuplot " + gplotFile;
        String[] path = {"", "/opt/local/bin/"}; // can be extended with additional prefixes
        Process pr = null;

        for (String prefix : path) {
            String cmd1 = prefix + cmd;
            try {
                pr = Runtime.getRuntime().exec(cmd1);
                try {
                    int ret = pr.waitFor();
                    if (ret != 0) {
                        throw new IllegalArgumentException("Executing command: " + cmd1 + " returned " + ret);
                    }
                } catch (InterruptedException ex) {
                    pr = null;
                }
                break;
            } catch (IOException ex) {
                // do nothing: try next path
            }
        }
        if (pr == null) {
            throw new IllegalArgumentException("Executing command: " + cmd + " with PATH=" + System.getenv().get("PATH"));
        }
        return resFile;
    }

    /*
     *  Returns a random integer in 0..size-1 according to the PDF 
     *
     *  Here we use a roulette-wheel selection in O(n) 
     *  (but practically faster since the shape of the PDF)
     *
     *  We could also use a binary search in O(log(size)) 
     *  for this we need to store the cumulative fct: pdfValue[x] = pdf(1) + ... + pdf(x) 
     *  NB: the CDF are known for classical PDF (but practically it is better to compute the array)
     */
    @Override
    public int randomInteger() {
        double prob = Utils.randomDouble(), fx;
        int x = 0;

        while ((fx = pdfValue[++x]) < prob) {
            prob -= fx;
        }

        return x - 1; // in 0..size-1
    }

    /**
     * @return the pdfName
     */
    @Override
    public String getName() {
        return pdfName;
    }

    /**
     * @return the monot
     */
    @Override
    public Monotonicity getMonot() {
        return monot;
    }

    /**
     * @return the size
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * @return the tau
     */
    @Override
    public double getTau() {
        return tau;
    }

    /**
     * @return the force
     */
    @Override
    public double getForce() {
        return force;
    }

    @Override
    public String toString() {
        return String.format("pdf: %s   size:%d   tau: %.6f   force: %.6f", pdfName, size, tau, force);
    }

    public static void main(String... args) throws FileNotFoundException, IOException {
        Locale.setDefault(new Locale("en", "US")); // at least for decimal point                                                      v

        OptionTable.VERBOSE_LEVEL.setValue(4);
        PDF p = new PDFPower(20, null, 0.5);
        String resFile = p.generateGnuPlotFiles("/tmp/foo", true);
        Utils.showFile(resFile);
        System.out.println(p);
    }
}
