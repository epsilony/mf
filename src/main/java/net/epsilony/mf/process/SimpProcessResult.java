/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpProcessResult implements ProcessResult {

    Matrix mainMatrix;
    boolean upperSymmetric;
    DenseVector generalForce;
    int nodeValueDimension;
    List<MFNode> nodes;

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
    public DenseVector getGeneralForce() {
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

    @Override
    public List<MFNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<MFNode> nodes) {
        this.nodes = nodes;
    }
}
