/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

import utils.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author diaz
 */
public class ProblemQAP extends AbstractProblem {

    private final String fileName;
    /*  
     * flow matrice
     */
    private int[][] matA;
    /*  
     * distance matrice
     */
    private int[][] matB;

    public ProblemQAP(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        loadFile(fileName);
    }

    /**
     * Load a QAP problem
     *
     * fileName: the file name of the QAP problem (can be a .dat or a .qap) qi:
     * the ptr to the info structure (can be NULL) the matrix a and b are not
     * allocated if the ptr != NULL at entry !
     *
     * Returns the size of the problem
     */
    private void loadFile(String fileName) throws FileNotFoundException {
        try (ProblemFileReader rd = new ProblemFileReader(new File(fileName))) {
            size = rd.readInteger("size");
            try (Scanner scan = new Scanner(rd.readLine())) { // use a try-with-resources
                int[] x = {0, 0};
                int nbX = 0;
                if (scan.hasNextInt()) {
                    x[nbX++] = scan.nextInt();
                }
                
                if (scan.hasNextInt()) {
                    x[nbX++] = scan.nextInt();
                }
                
                if (scan.hasNextInt()) {
                    nbX++;
                }
                
                switch (nbX) {
                    case 1:
                        bks = x[0]; // we suppose it is a BKS (not sure it is the optimum)
                        break;
                        
                    case 2: // the real .qap format
                        opt = x[0];
                        bks = x[1];
                        break;
                    default: // other strange format - don't use it
                        break;
                }
                
                if (opt < 0) {
                    bound = -opt;
                    opt = 0;
                } else {
                    bound = opt;
                }
            }
            
            Utils.displayMessage(1, "QAP file information", size);
            Utils.displayMessage(1, "size : %d", size);
            Utils.displayMessage(1, "opt : %d", opt);
            Utils.displayMessage(1, "bks : %d", bks);
            Utils.displayMessage(1, "bound : %d", bound);
            
            matA = rd.readMatrix(size, "matrix A");
            matB = rd.readMatrix(size, "matrix A");
        }
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the matA
     */
    public int[][] getMatA() {
        return matA;
    }

    /**
     * @return the matB
     */
    public int[][] getMatB() {
        return matB;
    }

}
