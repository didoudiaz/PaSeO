package pdf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import option.OptionTable;
import utils.Utils;

/**
 *
 * @author diaz
 */
public class PDFTable {

    private final static Double UNSET_DOUBLE = Double.NaN;

    private PDFTable() { // prevent instantiation
    }

    private static class MapKey {

        PDFCatalog pdfEntry;
        int size;
        Double tau; // use an object Double instead of a primitive type to avoid recomputing hashCode
        Double force; // use an object Double instead of a primitive type to avoid recomputing hashCode

        MapKey(PDFCatalog pdfEntry, int size, Double tau, Double force) {
            this.pdfEntry = pdfEntry;
            this.size = size;
            this.tau = (tau == null) ? UNSET_DOUBLE : tau;
            this.force = (force == null) ? UNSET_DOUBLE : force;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MapKey)) {
                return false;
            }
            MapKey k = (MapKey) o;
            return pdfEntry == k.pdfEntry && size == k.size && tau.equals(k.tau) && force.equals(k.force);
        }

        @Override
        public int hashCode() {
            int hash = (size << 4) | pdfEntry.ordinal();
            hash = 71 * hash + tau.hashCode();
            hash = 71 * hash + force.hashCode();
            return hash;
        }

        @Override
        public String toString() {
            return "pdfKey(" + pdfEntry.name() + "," + size + "," + tau + "," + force + ")";
        }

    }
    private static final ConcurrentHashMap<MapKey, PDF> cache = new ConcurrentHashMap<>();

    public static PDF createPDFInstance(PDFCatalog pdfEntry, int size, Double tau, Double force) {
        MapKey k = new MapKey(pdfEntry, size, tau, force);
        PDF pdf = cache.get(k);
        if (pdf == null) {
            pdf = pdfEntry.getCtorPdf().apply(size, tau, force);
            PDF pdf1 = cache.putIfAbsent(k, pdf); // here we use a putIfAbsent to avoid FindBugs reports (NB could be put() since the objects are the same even if created concurrently
            if (pdf1 != null) { // on has been created meanwhile by a another thread. Keep this
                pdf = pdf1; // only to be sure we return the one in the Map (not really needed in fact)
            }
        }
        return pdf;
    }

    public static PDF createPDFInstance(String pdfName, int size, Double tau, Double force) {
        PDFCatalog pdfEntry = PDFCatalog.getEntry(pdfName);
        if (pdfEntry == null) {
            return null;
        }
        return createPDFInstance(pdfEntry, size, tau, force);
    }

    public static void main(String... args) throws FileNotFoundException, IOException {
        Locale.setDefault(new Locale("en", "US")); // at least for decimal point                                                      v
        OptionTable.VERBOSE_LEVEL.setValue(4);
        PDF p = createPDFInstance(PDFCatalog.POWER, 20, 1.2, null);
        String resFile = p.generateGnuPlotFiles("/tmp/foo", false);
        if (resFile != null) {
            Utils.showFile(resFile);
        }
        System.out.println(p);

        PDF p1 = createPDFInstance("expon", 20, 1.2, null);
        System.out.println(p1);
    }
}
