
/*
 * Exponential law
 *
 * gnuplot:
 *
 * PDFexponential(x, tau) = exp(-tau * x)
 *
 * This is a special case of the Geometric PDF: 
 *
 * Geometric(x, p) = p * pow(1 - p, x) with p = 1 - exp(-tau)
 *
 * Indeed exp(-tau * x) = exp(log(exp(-tau)) * x) = pow(exp(-tau), x) 
 * which is basically a Geometric PDF with 1 - p = exp(-tau) => p = 1 - exp(-tau)
 *
 * More precisely:
 *
 * PDFexponential(x, tau) = Geometric(x, 1 - exp(-tau)) / (1 - exp(tau)) 
 * NB: 1 - exp(tau) being a constant factor it can be removed due to normalization. Thus:
 *
 * PDFexponential(x, tau) = Geometric(x, 1 - exp(-tau))
 */
package pdf;

/**
 *
 * @author diaz
 */
class PDFExponential extends AbstractPDF {

    public PDFExponential(int size, Double tauOptional, Double forceOptional) {
        super("Exponential", Monotonicity.FORCE_GROWS_AS_TAU, size, tauOptional, forceOptional,
                15.0 / size, //default tau
                EPSILON_FOR_FORCE, size);
    }

    @Override
    public double pdf(int x, double tau) {
        return Math.exp(-tau * x);		// tau > 0
    }
}
