
/*                                                                                                                  
 *  Probability Distribution Functions (PDF)                                                                        
 *                                                                                                                  
 *  NB: the term "density" is used for continuous functions,                                                        
 *               "distribution" is for discrete functions                                                           
 *                                                                                                                  
 *  More information on PDF:                                                                                        
 *  linux /usr/include/boost/math/distributions/ files *.hpp                                                        
 *  http://fr.mathworks.com/help/stats/pdf.html                                                                     
 */
package pdf;

import java.io.FileNotFoundException;

/**
 *
 * @author diaz
 */
public interface PDF {

    String getName();

    Monotonicity getMonot();

    int getSize();

    double getTau();

    double getForce();

    String generateGnuPlotFiles(String gplotPrefix, boolean histogram) throws FileNotFoundException;

    int randomInteger();
}
