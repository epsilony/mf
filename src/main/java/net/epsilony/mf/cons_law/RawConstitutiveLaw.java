/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import java.util.Arrays;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawConstitutiveLaw implements ConstitutiveLaw {

    DenseMatrix64F matrix;

    @Override
    public DenseMatrix64F getMatrix() {
        return matrix;
    }

    public RawConstitutiveLaw(DenseMatrix64F matrix) {
        if (matrix.getNumCols() != matrix.getNumRows()) {
            throw new IllegalArgumentException();
        }
        this.matrix = matrix;
    }

    @Override
    public double[] calcStressByEngineering(double[] strain, double[] result) {
        if (null == result) {
            result = new double[matrix.getNumRows()];
        } else {
            Arrays.fill(result, 0);
        }
        for (int i = 0; i < matrix.getNumRows(); i++) {
            for (int j = 0; j < matrix.getNumCols(); j++) {
                result[i] += matrix.get(i, j) * strain[j];
            }
        }
        return result;
    }

    @Override
    public void setDimension(int dim) {
        int[] dims = new int[]{1, 3, 6};
        if (dims[dim - 1] != matrix.getNumRows()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getDimension() {
        switch (matrix.getNumRows()) {
            case 6:
                return 3;
            case 3:
                return 2;
            case 1:
                return 1;
            default:
                throw new IllegalStateException();
        }
    }
}
