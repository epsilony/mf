/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPenaltyAssembler extends AbstractMechanicalAssembler<MechanicalPenaltyAssembler> {

    double penalty = 1e-6;

    public MechanicalPenaltyAssembler() {
    }

    public MechanicalPenaltyAssembler(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    @Override
    public void assembleDirichlet() {
        double[] dirichletVal = load;
        boolean[] dirichletMark = loadValidity;
        double factor = weight * penalty;
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        double[] lvs = testShapeFunctionValues[0];
        double[] rvs = trialShapeFunctionValues[0];

        final boolean dirichletX = dirichletMark[0];
        final boolean dirichletY = dirichletMark[1];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * 2;
            double lvi = lvs[i];
            if (dirichletX) {
                vec.add(row, lvi * dirichletVal[0] * factor);
            }
            if (dirichletY) {
                vec.add(row + 1, lvi * dirichletVal[1] * factor);
            }
            int jStart = 0;
            if (isUpperSymmetric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * 2;
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
                if (dirichletX) {
                    mat.add(tRow, tCol, vij);
                }
                if (dirichletY) {
                    mat.add(tRow + 1, tCol + 1, vij);
                }
            }
        }
    }

    @Override
    protected int getMainMatrixSize() {
        return nodesNum * 2;
    }

    @Override
    public MechanicalPenaltyAssembler synchronizeClone() {
        MechanicalPenaltyAssembler result = new MechanicalPenaltyAssembler(penalty);
        result.setNodesNum(nodesNum);
        result.setConstitutiveLaw(constitutiveLaw);
        result.setMatrixDense(dense);
        result.prepare();
        return result;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    @Override
    public int getNodeValueDimension() {
        return 2;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, mat dense/sym: %b/%b, penalty %f}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getPenalty());
    }
}
