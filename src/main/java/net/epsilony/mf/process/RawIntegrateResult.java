/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawIntegrateResult implements IntegrateResult {

    Matrix mainMatrix;
    boolean upperSymmetric;
    DenseVector generalForce;
    int nodeValueDimension;

    @Override
    public Matrix getMainMatrix() {
        return mainMatrix;
    }

    public void setMainMatrix(Matrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    @Override
    public boolean isUpperSymmetric() {
        return upperSymmetric;
    }

    public void setUpperSymmetric(boolean upperSymmetric) {
        this.upperSymmetric = upperSymmetric;
    }

    @Override
    public DenseVector getMainVector() {
        return generalForce;
    }

    public void setGeneralForce(DenseVector generalForce) {
        this.generalForce = generalForce;
    }

    @Override
    public int getNodeValueDimension() {
        return nodeValueDimension;
    }

    public void setNodeValueDimension(int nodeValueDimension) {
        this.nodeValueDimension = nodeValueDimension;
    }
}
