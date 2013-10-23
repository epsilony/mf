/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPenaltyAssembler extends AbstractMechanicalAssembler {

    double penalty = 1e-6;

    public MechanicalPenaltyAssembler() {
    }

    public MechanicalPenaltyAssembler(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    @Override
    public void assembleDirichlet() {
        double factor = weight * penalty;
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        double[] lvs = testShapeFunctionValues[0];
        double[] rvs = trialShapeFunctionValues[0];

        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * valueDimension;
            double lvi = lvs[i];
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValidity[dim]) {
                    vec.add(row + dim, lvi * load[dim] * factor);
                }
            }
            int jStart = 0;
            if (isUpperSymmetric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * valueDimension;
                double vij = factor * lvi * rvs[j];
                int tRow;
                int tCol;
                if (isUpperSymmetric() && col <= row) {
                    tRow = col;
                    tCol = row;
                } else {
                    tRow = row;
                    tCol = col;
                }
                for (int dim = 0; dim < valueDimension; dim++) {
                    mat.add(tRow + dim, tCol + dim, vij);
                }
            }
        }
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*dim: %d*%d, mat dense/sym: %b/%b, penalty %f}",
                getNodesNum(),
                getSpatialDimension(),
                isMatrixDense(),
                isUpperSymmetric(),
                getPenalty());
    }
}
