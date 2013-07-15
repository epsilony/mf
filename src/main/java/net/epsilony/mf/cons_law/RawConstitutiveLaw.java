/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawConstitutiveLaw implements ConstitutiveLaw {

    DenseMatrix matrix;

    @Override
    public DenseMatrix getMatrix() {
        return matrix;
    }

    public RawConstitutiveLaw(DenseMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public double[] calcStress(double[] strain, double[] result) {
        if (null == result) {
            result = new double[3];
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i] += matrix.get(i, j) * strain[j];
            }
        }
        return result;
    }
}
