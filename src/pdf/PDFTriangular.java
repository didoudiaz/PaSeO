/*
 *  Triangular law
 *
 *  gnuplot:
 *
 *  Triangular(x, a, c, b) = (x <= a || x >= b) ? 0.0 : (x <= c) ?  2.0 * (x - a) / ((b - a) * (c - a)) : 2.0 * (b - x) / ((b - a) * (b - c))
 *  
 *  PDFtriangular(x, tau) = Triangular(x, 0, 1, tau)
 */
package pdf;

/**
 *
 * @author diaz
 */
public class PDFTriangular extends AbstractPDF {

    public PDFTriangular(int size, Double tauOptional, Double forceOptional) {
        super("Triangular", Monotonicity.FORCE_GROWS_AS_INV_TAU, size, tauOptional, forceOptional,
                size / 5.0, // default tau
                0, size);
    }

    @Override
    public double pdf(int x, double tau) {
        return triangular(x, 0, 1, tau);
    }

    public static double triangular(double x, double a, double c, double b) {
        if (x <= a || x >= b) {
            return 0;
        }

        if (x <= c) {
            return 2.0 * (x - a) / ((b - a) * (c - a));
        } else {
            return 2.0 * (b - x) / ((b - a) * (b - c));
        }
    }
}
