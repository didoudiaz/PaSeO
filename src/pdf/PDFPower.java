
/*
 * Power law (original PDF proposed for EO by Stefan Boettcher)
 *
 * gnuplot (having defined pow(x,y)=x ** y): 
 *
 * PDFpower(x, tau) = pow(x, -tau)
 *
 * It is a special case of the Beta PDF: 
 *
 * Beta(x,alpha,beta) = constant * pow(x, alpha - 1) * pow(1 - x, beta - 1) 
 *
 * with beta = 1 and alpha = 1 - tau
 *
 * NB: "constant" is a normalization constant (s.t. the total probability sums to 1) 
 * it is usually the noted 1 / B(alpha,beta) where B(alpha,beta) is the
 * Legendre’s beta-function :
 *
 * B(alpha,beta) = (gamma(alpha) * gamma(beta)) / gamma(alpha+beta) 
 *
 * where gamma(k) is the standard $\Gamma$-function, NB: for integers gamma(k) = (k - 1)! 
 *
 * Then if beta = 1 then B(alpha,beta) = 1 and "constant" = 1. Thus:
 *
 * PDFpower(x, tau) = Beta(x, 1 - tau, 1)
 */
package pdf;

/**
 *
 * @author diaz
 */
class PDFPower extends AbstractPDF {

    public PDFPower(int size, Double tauOptional, Double forceOptional) {
        super("Power", Monotonicity.FORCE_GROWS_AS_TAU, size, tauOptional, forceOptional,
                1.0 + 1.0 / Math.log(size), // default tau: proposed by S. Boettcher for EO
                EPSILON_FOR_FORCE, size);
    }

    @Override
    public double pdf(int x, double tau) {
        return Math.pow(x, -tau);		// tau > 0
    }
}
