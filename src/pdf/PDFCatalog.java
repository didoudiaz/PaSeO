package pdf;

import java.util.Locale;
import utils.Lambda3;

/**
 *
 * @author diaz
 */
public enum PDFCatalog {

    POWER(PDFPower::new),
    EXPONENTIAL(PDFExponential::new),
    NORMAL(PDFNormal::new),
    GAMMA(PDFGamma::new),
    CAUCHY(PDFCauchy::new),
    TRIANGULAR(PDFTriangular::new);

    public static final PDFCatalog DEFAULT_PDF = POWER;

    private final Lambda3<Integer, Double, Double, PDF> ctorPDF;

    PDFCatalog(Lambda3<Integer, Double, Double, PDF> ctorPDF) {
        this.ctorPDF = ctorPDF;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    /**
     * @return the ctorPDF
     */
    public Lambda3<Integer, Double, Double, PDF> getCtorPdf() {
        return ctorPDF;
    }
    
    
    public static PDFCatalog getEntry(String name) {
        for (PDFCatalog entry : values()) {
            if (entry.name().substring(0, name.length()).equalsIgnoreCase(name)) {
                return entry;
            }
        }
        return null;
    }
}
