/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WrapperMFMatrixFactory implements MatrixFactory<MFMatrix> {

    int numRows;
    int numCols;
    Class<?> matrixClass;

    @Override
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    @Override
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public void setMatrixClass(Class<?> matrixClass) {
        this.matrixClass = matrixClass;
    }

    @Override
    public MFMatrix produce() {
        Object produceMatrix = MFMatries.produceMatrix(numRows, numCols, matrixClass);
        return MFMatries.wrap(produceMatrix, matrixClass);
    }

}
