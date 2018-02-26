package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import option.OptionTable;

/**
 *
 * @author diaz
 */
public class Utils {

    // Suppresses default constructor, ensuring non-instantiability.
    private Utils() {
    }

    public static int getPID() {
        // in java 9 see ProcessHandle.current().getPid()
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    public static boolean isMacOs() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return osName.startsWith("mac os x");
    }

    /**
     * Return the elapsed time since start (wall time)
     *
     * @return the elapsed time
     */
    public static long getElapsedTime() {
        long time = System.currentTimeMillis() - START_TIME;
        //       long time = System.nanoTime() / 1000000;
        return time;
    }

    private static final long START_TIME = getElapsedTime();

    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static int randomInteger(int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }

    public static int randomInterval(int inf, int sup) {
        return ThreadLocalRandom.current().nextInt(inf, sup + 1);
    }

    /*
     * Generate a vector of size elements with a random permutation:
     * - of values in baseValue..baseValue+size-1 (without actualValue)
     * - of values in actualValue[] + baseValue
     * 
     * Use the following shuffle (Durstenfeld implementation of Fisher-Yates
     * shuffle):
     * 
     *  vec[0] = source[0];
     *  for(i = 1; i < size; i++) {
     *    j = random number in [0..i]
     *    vec[i] = vec[j];
     *    vec[j] = source[i];
     *  }
     */
    public static void randomPermut(int[] vec) {
        randomPermut(vec, 0);
    }

    public static void randomPermut(int[] vec, int baseValue) {
        vec[0] = baseValue;
        for (int i = 1; i < vec.length; i++) {
            int j = randomInteger(i + 1);
            vec[i] = vec[j];
            vec[j] = baseValue + i;
        }
    }

    public static void randomPermut(int[] vec, int[] actualValue) {
        randomPermut(vec, actualValue, 0);
    }

    public static void randomPermut(int[] vec, int[] actualValue, int baseValue) {
        vec[0] = baseValue + actualValue[0];
        for (int i = 1; i < vec.length; i++) {
            int j = randomInteger(i + 1);
            vec[i] = vec[j];
            vec[j] = baseValue + actualValue[i];
        }
    }

    /*
     * Generate a random permutation of a given vector of size elements.
     *
     * Use the following shuffle (Durstenfeld implementation of Fisher-Yates
     * shuffle)
     *
     * To shuffle an array a of n elements:
     *
     *  for(i = size − 1; i >= 1; i--) {
     *    j = random number in [0..i]
     *    swap vec[i] and vec[j]
     *  }
     */
    public static void randomArrayPermut(int[] vec) {
        for (int i = vec.length - 1; i > 0; i--) {
            int j = randomInteger(i + 1);
            int z = vec[i];
            vec[i] = vec[j];
            vec[j] = z;
        }
    }

    public static int[][] allocMatrix(int size) {
        return new int[size][size];
    }

    public static int[] allocVector(int size) {
        return new int[size];
    }

    public static void copyVector(int[] src, int[] dst) {
        System.arraycopy(src, 0, dst, 0, src.length);
    }

    public static void fillVector(int[] vec, int value) {
        int len = vec.length;
        if (len > 0) {
            vec[0] = value;
        }
        for (int i = 1; i < len; i += i) {
            System.arraycopy(vec, 0, vec, i, ((len - i) < i) ? (len - i) : i);
        }
    }

    public static void zeroVector(int[] vec) {
        fillVector(vec, 0);
    }

    public static void displayVector(int[] vec) {
        displayVector(0, vec);
    }

    public static void displayVector(int level, int[] vec) {
        if ((int) OptionTable.VERBOSE_LEVEL.getValue() >= level) {
            StringBuilder buff = new StringBuilder(); // create a string and then display to avoid interleaved displays by threads
            for (int x : vec) {
                buff.append(x).append(" ");
            }
            System.out.println(buff);
        }
    }

    public static void swap(int[] vec, int i, int j) {
        int tmp = vec[i];
        vec[i] = vec[j];
        vec[j] = tmp;
    }

    public static void displayMessage(int level, String format, Object... args) {
        displayMessage(level, -1, null, format, args);
    }

    public static void displayMessage(int level, int solverNo, String solverName, String format, Object... args) {
        if ((int) OptionTable.VERBOSE_LEVEL.getValue() >= level) {
            if (solverName != null) {
                System.out.printf(solverName + "#" + solverNo + ":: " + format + "%n", args);
            } else {
                System.out.printf(format + "%n", args);
            }
        }
    }

    public static String convertToCamelCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean convert = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ' ' || c == '-' || c == '_') {
                convert = true;
            } else if (!convert) {
                sb.append(c);
            } else {
                sb.append(Character.toUpperCase(c));
                convert = false;
            }
        }
        return sb.toString();
    }

    public static boolean showFile(String resFile) throws IOException {
        if (Desktop.isDesktopSupported()) {
            File myFile = new File(resFile);
            Desktop.getDesktop().open(myFile);
            return true;
        }
        return false;
    }
}
