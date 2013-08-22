/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMechanicalAssembler<T extends MechanicalAssembler<T>>
        extends AbstractAssembler<T> implements MechanicalAssembler<T> {

    protected ConstitutiveLaw constitutiveLaw;
    boolean upperSymmetric = true;

    public AbstractMechanicalAssembler() {
    }

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    @Override
    public boolean isUpperSymmetric() {
        return upperSymmetric;
    }

    @Override
    public void assembleNeumann() {
        DenseVector vec = mainVector;
        double[] neumannVal = load;
        double valueX = neumannVal[0] * weight;
        double valueY = neumannVal[1] * weight;
        double[] vs = testShapeFunctionValues[0];
        final boolean vali1 = valueX != 0;
        final boolean vali2 = valueY != 0;
        TIntArrayList indes = testAssemblyIndes;
        for (int i = 0; i < indes.size(); i++) {
            int vecIndex = indes.getQuick(i) * 2;
            double v = vs[i];
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
//        double[] rv = trialShapeFunctionValues[0];
        double[] rv_x = trialShapeFunctionValues[1];
        double[] rv_y = trialShapeFunctionValues[2];
        double[] lv = testShapeFunctionValues[0];
        double[] lv_x = testShapeFunctionValues[1];
        double[] lv_y = testShapeFunctionValues[2];
        double b1 = 0;
        double b2 = 0;
        if (volumnForce != null) {
            b1 = volumnForce[0] * weight;
            b2 = volumnForce[1] * weight;
        }
        Matrix mat = mainMatrix;
        for (int i = 0; i < testAssemblyIndes.size(); i++) {
            int row = testAssemblyIndes.getQuick(i) * 2;
            double lv_x_i = lv_x[i];
            double lv_y_i = lv_y[i];
            double lv_i = lv[i];
            if (volumnForce != null) {
                mainVector.add(row, b1 * lv_i);
                mainVector.add(row + 1, b2 * lv_i);
            }
            int jStart = 0;
            if (isUpperSymmetric()) {
                jStart = i;
            }
            double[] i_v1 = new double[]{lv_x_i, 0, lv_y_i};
            double[] i_v2 = new double[]{0, lv_y_i, lv_x_i};
            for (int j = jStart; j < trialAssemblyIndes.size(); j++) {
                int col = trialAssemblyIndes.getQuick(j) * 2;
                double rv_x_j = rv_x[j];
                double rv_y_j = rv_y[j];
                double[] j_v1 = new double[]{rv_x_j, 0, rv_y_j};
                double[] j_v2 = new double[]{0, rv_y_j, rv_x_j};
                double d11 = weight * multConstitutiveLaw(i_v1, j_v1);
                double d21 = weight * multConstitutiveLaw(i_v2, j_v1);
                double d12 = weight * multConstitutiveLaw(i_v1, j_v2);
                double d22 = weight * multConstitutiveLaw(i_v2, j_v2);
                if (isUpperSymmetric() && col <= row) {
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
                    if (!(isUpperSymmetric() && row == col)) {
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
        double[] calcStress = constitutiveLaw.calcStressByEngineering(right, null);
        double result = 0;
        for (int i = 0; i < 3; i++) {
            result += left[i] * calcStress[i];
        }
        return result;
    }
}
