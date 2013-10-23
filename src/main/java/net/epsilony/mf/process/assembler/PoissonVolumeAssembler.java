/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PoissonVolumeAssembler extends AbstractAssembler {

    public PoissonVolumeAssembler() {
        valueDimension = 1;
    }

    @Override
    public void assemble() {
        for (int testPos = 0; testPos < nodesAssemblyIndes.size(); testPos++) {
            assembleVolumeVectorElem(testPos);
            for (int trialPos = 0; trialPos < nodesAssemblyIndes.size(); trialPos++) {
                assembleVolumeMatrixElem(testPos, trialPos);
            }
        }
    }

    private void assembleVolumeVectorElem(int testPos) {
        if (load == null) {
            return;
        }
        int row = nodesAssemblyIndes.getQuick(testPos);
        mainVector.add(row, 0, testShapeFunctionValues[0][testPos] * weight * load[0]);
    }

    private void assembleVolumeMatrixElem(int testPos, int trialPos) {

        int col = nodesAssemblyIndes.getQuick(trialPos);
        int row = nodesAssemblyIndes.getQuick(testPos);
        if (mainMatrix.isUpperSymmetric() && col < row) {
            return;
        }
        double value = 0;
        for (int spatialDim = 0; spatialDim < spatialDimension; spatialDim++) {
            value += testShapeFunctionValues[spatialDim + 1][testPos] * trialShapeFunctionValues[spatialDim + 1][trialPos];
        }
        mainMatrix.add(row, col, value * weight);
    }

    @Override
    public int getValueDimension() {
        return super.getValueDimension();
    }

    @Override
    public void setValueDimension(int valueDimension) {
        if (valueDimension != 1) {
            throw new IllegalArgumentException();
        }
        super.setValueDimension(valueDimension);
    }
}
