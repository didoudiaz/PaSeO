/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

import utils.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author diaz
 */
public class ProblemFileReader implements AutoCloseable {

    private final String fileName;
    private final Scanner scan;

    public ProblemFileReader(File file) throws FileNotFoundException {
        this.fileName = file.getName();
        scan = new Scanner(file, "UTF-8");
    }

    @Override
    public void close() {
        scan.close();
    }

    public int readInteger(String what) {
        try {
            return scan.nextInt();
        } catch (NoSuchElementException ex) { // this includes InputMismatchException which is a subclass of NoSuchElementException
            throw new NumberFormatException(fileName + ": Could not read an integer for '" + what + "'");
        }
    }

    public int[][] readMatrix(int size, String what) {
        int[][] mat = Utils.allocMatrix(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mat[i][j] = readInteger(what);
            }
        }
        return mat;
    }

    public String readLine() {
        return scan.nextLine();
    }

}
