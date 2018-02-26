/*
 *  Cauchy law
 *
 *  gnuplot:
 *
 *  Cauchy(x, x0, a) = (1.0 / pi) * (a / ((x - x0) ** 2.0 + a * a))
 *
 *  PDFcauchy(x, tau) = Cauchy(x, 1, tau)
 */
package pdf;

/**
 *
 * @author diaz
 */
public class PDFCauchy extends AbstractPDF {

    public PDFCauchy(int size, Double tauOptional, Double forceOptional) {
        super("Cauchy", Monotonicity.FORCE_GROWS_AS_INV_TAU, size, tauOptional, forceOptional,
                size / 22.22, // default tau
                0, size);
    }

    @Override
    public double pdf(int x, double tau) {
        return cauchy(x, 1, tau);
    }

    public static double cauchy(double x, double x0, double a) {
        double f;

        // f = (1.0 / M_PI) * (a / (pow(x - x0 , 2) + a * a));
        f = 0.31830988618379067154 * (a / (Math.pow(x - x0, 2) + a * a));

        return f;
    }
}
