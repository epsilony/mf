/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMechanicalAssembler<T extends MechanicalAssembler<T>>
        extends AbstractAssembler<T> implements MechanicalAssembler<T> {

    protected ConstitutiveLaw constitutiveLaw;
    protected DenseMatrix constitutiveLawMatrixCopy;

    public AbstractMechanicalAssembler() {
    }

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
        constitutiveLawMatrixCopy = new DenseMatrix(constitutiveLaw.getMatrix());
    }

    @Override
    public boolean isUpperSymmertric() {
        return constitutiveLaw.isSymmetric();
    }

    @Override
    public void assembleNeumann() {
        DenseVector vec = mainVector;
        double[] neumannVal = load;
        double valueX = neumannVal[0] * weight;
        double valueY = neumannVal[1] * weight;
        TDoubleArrayList vs = shapeFunctionValues[0];
        final boolean vali1 = valueX != 0;
        final boolean vali2 = valueY != 0;
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int vecIndex = nodesAssemblyIndes.getQuick(i) * 2;
            double v = vs.getQuick(i);
            if (vali1) {
                vec.add(vecIndex, valueX * v);
            }
            if (vali2) {
                vec.add(vecIndex + 1, valueY * v);
            }
        }
    }

    @Override
    public void assembleVolume() {
        double[] volumnForce = load;
        TDoubleArrayList v = shapeFunctionValues[0];
        TDoubleArrayList v_x = shapeFunctionValues[1];
        TDoubleArrayList v_y = shapeFunctionValues[2];
        double b1 = 0;
        double b2 = 0;
        if (volumnForce != null) {
            b1 = volumnForce[0] * weight;
            b2 = volumnForce[1] * weight;
        }
        Matrix mat = mainMatrix;
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * 2;
            double v_x_i = v_x.getQuick(i);
            double v_y_i = v_y.getQuick(i);
            double v_i = v.getQuick(i);
            if (volumnForce != null) {
                mainVector.add(row, b1 * v_i);
                mainVector.add(row + 1, b2 * v_i);
            }
            int jStart = 0;
            if (isUpperSymmertric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * 2;
                double v_x_j = v_x.getQuick(j);
                double v_y_j = v_y.getQuick(j);
                double[] i_v1 = new double[]{v_x_i, 0, v_y_i};
                double[] i_v2 = new double[]{0, v_y_i, v_x_i};
                double[] j_v1 = new double[]{v_x_j, 0, v_y_j};
                double[] j_v2 = new double[]{0, v_y_j, v_x_j};
                double d11 = weight * multConstitutiveLaw(i_v1, j_v1);
                double d21 = weight * multConstitutiveLaw(i_v2, j_v1);
                double d12 = weight * multConstitutiveLaw(i_v1, j_v2);
                double d22 = weight * multConstitutiveLaw(i_v2, j_v2);
                if (isUpperSymmertric() && col <= row) {
                    mat.add(col, row, d11);
                    mat.add(col, row + 1, d21);
                    mat.add(col + 1, row + 1, d22);
                    if (row != col) {
                        mat.add(col + 1, row, d12);
                    }
                } else {
                    mat.add(row, col, d11);
                    mat.add(row, col + 1, d12);
                    mat.add(row + 1, col + 1, d22);
                    if (!(isUpperSymmertric() && row == col)) {
                        mat.add(row + 1, col, d21);
                    }
                }
            }
        }
    }

    @Override
    public int getDirichletDiffOrder() {
        return 0;
    }

    @Override
    public int getVolumeDiffOrder() {
        return 1;
    }

    protected double multConstitutiveLaw(double[] left, double[] right) {
        DenseMatrix mat = constitutiveLawMatrixCopy;
        double result = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double t = left[i] * right[j];
                if (t != 0) {
                    result += t * mat.get(i, j);
                }
            }
        }
        return result;
    }
}
