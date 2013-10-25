/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PlaneStress implements ConstitutiveLaw {

    private final double youngModulity, poissonRatio;
    private final WrapperMFMatrix<DenseMatrix64F> matrixWrapper;
    private final DenseMatrix64F matrix;

    public MFMatrix getMatrix() {
        return matrixWrapper;
    }

    public PlaneStress(double youngModulity, double poissonRatio) {
        this.youngModulity = youngModulity;
        this.poissonRatio = poissonRatio;
        double t = youngModulity / (1 - poissonRatio * poissonRatio);
        matrix = new DenseMatrix64F(3, 3);
        matrix.set(0, 0, t);
        matrix.set(0, 1, poissonRatio * t);
        matrix.set(1, 0, poissonRatio * t);
        matrix.set(1, 1, t);
        matrix.set(2, 2, (1 - poissonRatio) / 2 * t);
        matrixWrapper = MFMatries.wrap(matrix);
    }

    public double getYoungModulity() {
        return youngModulity;
    }

    public double getNu() {
        return poissonRatio;
    }

    @Override
    public double[] calcStressByEngineeringStrain(double[] strain, double[] result) {

        double s11 = matrix.get(0, 0) * strain[0] + matrix.get(0, 1) * strain[1];
        double s22 = matrix.get(1, 0) * strain[0] + matrix.get(1, 1) * strain[1];
        double s12 = matrix.get(2, 2) * strain[2];
        if (null == result) {
            result = new double[]{s11, s22, s12};
        } else {
            result[0] = s11;
            result[1] = s22;
            result[2] = s12;
        }
        return result;
    }

    @Override
    public void setDimension(int dim) {
        if (dim != 2) {
            throw new IllegalArgumentException("PlaneStress only supports dimension 2");
        }
    }

    @Override
    public int getDimension() {
        return 2;
    }
}
