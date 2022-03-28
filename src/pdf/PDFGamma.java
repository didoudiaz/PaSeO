/*
 *  Gamma law
 *
 *  gnuplot:
 *
 *  Gamma(x, k, theta) = 1.0 / (gamma(k) * pow(theta, k)) * pow(x, k - 1) * exp(-x / theta)
 *
 *  PDFgamma(x, tau) = Gamma(x, tau, exp(tau))
 */
package pdf;

/**
 *
 * @author diaz
 */
public class PDFGamma extends AbstractPDF {

//   non-monotone: cannot apply binary search: here is a regression formula
//   tau = 0.5304325176 * Math.log(size) - 3.387043916 * force + 1.123443686;
//   NB: tau can be < 0, it is OK
    public PDFGamma(int size, Double tauOptional, Double forceOptional) {
        super("Gamma", Monotonicity.NON_MONOTONE, size, tauOptional, forceOptional,
                0.5304325176 * Math.log(size) - 0.9087826636, // default tau: force = 0.6
                EPSILON_FOR_FORCE, 10);
    }

    @Override
    public double pdf(int x, double tau) {
        //double theta = log(size);
        //double theta = log(size);
        //double theta = 4;//log(size / (tau + 1));

        double k = tau;
        double theta = Math.exp(tau);

        return gamma(x, k, theta);
    }

    public static double gamma(double x, double k, double theta) {
        return Math.pow(x, k - 1) * Math.exp(-x / theta) / (Math.pow(theta, k) * gamma(k));
    }

    /*
     * The Gamma function is equivalent to the factorial for integer arguments, 
     * but is valid for most real arguments > 0.  The gamma function is defined as:
     *
     * gamma(x) = integral( e^(-t) * t^(x-1), t in 0 .. +oo)
     * defined for for x > 0 
     *
     * Remark: if x is an integer, gamma(x) = (x - 1)! (use rint() on result)
     *
     * The following code uses Lanczos approximation 
     * for the coefficient table see http://mrob.com/pub/ries/lanczos-gamma.html
     * Tables are provided for some values of g (see code) and n (table size = gX.length)
     */
//    private final static double[] COEFF = { // coeff for g=7 and n=9, used by GNU scientific library
//        0.99999999999980993,
//        676.5203681218851,
//        -1259.1392167224028,
//        771.32342877765313,
//        -176.61502916214059,
//        12.507343278686905,
//        -0.13857109526572012,
//        9.9843695780195716e-6,
//        1.5056327351493116e-7};
//    final static double G = 7; // the g value associated to the coeff table
    private final static double[] COEFF = { // table for g=9 and n=10
        1.000000000000000174663,
        5716.400188274341379136,
        -14815.30426768413909044,
        14291.49277657478554025,
        -6348.160217641458813289,
        1301.608286058321874105,
        -108.1767053514369634679,
        2.605696505611755827729,
        -0.7423452510201416151527e-2,
        0.5384136432509564062961e-7,
        -0.4023533141268236372067e-8};
    final static double G = 9; // the g value associated to the coeff table

    public static double gamma(double x) {
        double xx = x;
        if (xx < 0.5) {
            return Math.PI / (Math.sin(Math.PI * xx) * gamma(1.0 - xx));
        }
        xx -= 1.0;
        double a = COEFF[0];
        for (int i = 1; i < COEFF.length; i++) {
            a += COEFF[i] / (xx + i);
        }
        double t = xx + G + 0.5;
        double r = Math.sqrt(2.0 * Math.PI) * Math.pow(t, xx + 0.5) * Math.exp(-t) * a;
//        if (Math.floor(x) == x && x <= 18) { // provides exact factorial(n) n in 0..17 (x in 1..18)
//            r = Math.rint(r);
//        }
        return r;
    }

    public static void main(String... args) {
        for (double x = 0; x <= 10.5; x += 0.2) {
            System.out.printf("%.20f  %.17f%n", x, gamma(x));
        }
//        System.out.println("--- integers (factorial) : precise");
//        for (int x = 0; x <= 17; x++) {
//            System.out.printf("%d %.17f%n", x, gamma(x));
//        }
//        System.out.println("--- integers (factorial) : approx");
//        for (int x = 18; x <= 30; x++) {
//            System.out.printf("%d %.17g%n", x, gamma(x));
//        }
    }
}
