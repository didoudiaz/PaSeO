/*
 *  Normal (Gaussian) law
 *
 *  gnuplot:
 *
 *  Normal(x, mu, sigma) = (1.0 / (sigma * sqrt(2.0 * pi)) * exp(-0.5 * ((x - mu) / sigma) ** 2.0))
 *
 *  PDFnormal(x, tau) = Normal(x, 1, tau)
 */
package pdf;

/**
 *
 * @author diaz
 */
public class PDFNormal extends AbstractPDF {

    public PDFNormal(int size, Double tauOptional, Double forceOptional) {
        super("Normal", Monotonicity.FORCE_GROWS_AS_INV_TAU, size, tauOptional, forceOptional,
                Math.log(size), // default tau
                0, size * Math.log(size));
    }

    @Override
    public double pdf(int x, double tau) {
        return normal(x, 1, tau);
    }

    public static double normal(double x, double mu, double sigma) {
        double f;

        //  f = 1.0 / (sigma * Math.sqrt(2.0 * M_PI)) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2.0));
        f = 1.0 / (sigma * 2.506628274631) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2.0));

        return f;
    }
}
