/*
 */
package solver;

import cooperation.ElitePool;
import option.OptionTable;
import pdf.PDF;
import pdf.PDFTable;
import problem.Model;

/**
 *
 * @author diaz
 */
public class ExtremalOptimization extends AbstractIterativeSearch {

    private String pdfName;
    private final PDF pdf;
    private double tau;
    private double force;

    private final FitnessTable<Move> fitList;

    public ExtremalOptimization(Model model, ElitePool elitePool) {
        super(model, elitePool, SolverCatalog.EO);

        pdfName = (String) OptionTable.PDF_NAME.getValue();
        Double tauOptional = (Double) OptionTable.TAU.getValue();
        Double forceOptional = (Double) OptionTable.FORCE.getValue();
        pdf = PDFTable.createPDFInstance(pdfName, size, tauOptional, forceOptional);
        if (pdf == null) {
            throw new IllegalArgumentException("unknown PDF: " + pdfName);
        }
        pdfName = pdf.getName();
        tau = pdf.getTau();
        force = pdf.getForce();

        displayMessage(1, "PDF  : %s", pdfName);
        displayMessage(1, "tau  : %.6f", tau);
        displayMessage(1, "force: %.6f", force);

        this.fitList = new FitnessTableImpl<>(size);
    }

    @Override
    protected int doIteration(int currentIteration, int currentCost, int[] sol) {
        Neighborhood neighborhood = model.neighborhood();

        fitList.clear();

        for (Move move : neighborhood) {
            //System.out.println("the move " + move);
            int costMove = model.costOfMove(currentCost, sol, move);
//            Move move1 = null;
//            try {
//                move1 = move.clone();
//            } catch (CloneNotSupportedException ex) {
//                System.err.println("CLONING PB: " + ex);
//                System.exit(1);
//            }
            for (PairVariableValue vv : move.getAssignments(sol, vvPool)) {
                fitList.record(vv.getValue(), -costMove, move);
            }
        }

        fitList.sort();

        int rank = pdf.randomInteger();
//        System.out.println("rank = " + rank);

        Move move = fitList.getInfo(rank);

        //Utils.displayMessage(3, "Selected Move %s", move.toString());

        int cost = model.doMove(currentCost, sol, move);
//        Utils.displayMessage(3, "new cost %d", cost);
//        Utils.displayVector(3, sol);
//        System.out.println("computed cost " + model.costOfSolution(sol));

        return cost;
    }

    @Override
    protected int afterAdoptedSolution(int currentCost, int[] sol) {
        return currentCost;
    }
}
