/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PenaltyDirichletAssembler extends AbstractAssembler {

    double penalty = 1e-6;

    public PenaltyDirichletAssembler() {
    }

    public PenaltyDirichletAssembler(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    @Override
    public void assemble() {
        double factor = weight * penalty;
        MFMatrix mat = mainMatrix;
        MFMatrix vec = mainVector;
        double[] lvs = testShapeFunctionValues[0];
        double[] rvs = trialShapeFunctionValues[0];

        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * valueDimension;
            double lvi = lvs[i];
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValidity[dim]) {
                    vec.add(row + dim, 0, lvi * load[dim] * factor);
                }
            }
            int jStart = 0;
            if (mainMatrix.isUpperSymmetric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * valueDimension;
                double vij = factor * lvi * rvs[j];
                int tRow;
                int tCol;
                if (mainMatrix.isUpperSymmetric() && col <= row) {
                    tRow = col;
                    tCol = row;
                } else {
                    tRow = row;
                    tCol = col;
                }
                for (int dim = 0; dim < valueDimension; dim++) {
                    if (!loadValidity[dim]) {
                        continue;
                    }
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
                + String.format("{nodes*dim: %d*%d, penalty %f}",
                        getNodesNum(),
                        getSpatialDimension(),
                        getPenalty());
    }
}
