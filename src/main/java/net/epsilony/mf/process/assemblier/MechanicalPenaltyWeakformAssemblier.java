/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPenaltyWeakformAssemblier extends AbstractMechanicalWeakformAssemblier<MechanicalPenaltyWeakformAssemblier> {

    double penalty = 1e-6;

    public MechanicalPenaltyWeakformAssemblier() {
    }

    public MechanicalPenaltyWeakformAssemblier(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    @Override
    public void assembleDirichlet() {
        double[] dirichletVal = load;
        boolean[] dirichletMark = loadValidity;
        double factor = weight * penalty;
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        TDoubleArrayList vs = shapeFunctionValues[0];

        final boolean dirichletX = dirichletMark[0];
        final boolean dirichletY = dirichletMark[1];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * 2;
            double vi = vs.getQuick(i);
            if (dirichletX) {
                vec.add(row, vi * dirichletVal[0] * factor);
            }
            if (dirichletY) {
                vec.add(row + 1, vi * dirichletVal[1] * factor);
            }
            int jStart = 0;
            if (isUpperSymmertric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * 2;
                double vij = factor * vi * vs.getQuick(j);
                int tRow;
                int tCol;
                if (isUpperSymmertric() && col <= row) {
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
    public MechanicalPenaltyWeakformAssemblier synchronizeClone() {
        MechanicalPenaltyWeakformAssemblier result = new MechanicalPenaltyWeakformAssemblier(penalty);
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
                isUpperSymmertric(),
                getPenalty());
    }
}
