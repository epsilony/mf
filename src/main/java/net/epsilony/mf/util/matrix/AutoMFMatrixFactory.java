/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrixFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AutoMFMatrixFactory implements MatrixFactory<MFMatrix> {

    int numRows;
    int numCols;
    Class<?> matrixClass;
    MatrixFactory innerFactory;

    public AutoMFMatrixFactory(Class<?> matrixClass) {
        _setMatrixClass(matrixClass);
    }

    public AutoMFMatrixFactory() {
    }

    @Override
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    @Override
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public void setMatrixClass(Class<?> matrixClass) {
        _setMatrixClass(matrixClass);
    }

    private void _setMatrixClass(Class<?> matrixClass) {
        this.matrixClass = matrixClass;
        if (MFMatrix.class.isAssignableFrom(matrixClass)) {
            SimpMFMatrixFactory simpMFMatrixFactory = new SimpMFMatrixFactory();
            simpMFMatrixFactory.setMatrixClass(matrixClass);
            innerFactory = simpMFMatrixFactory;
        } else {
            WrapperMFMatrixFactory wrapperMFMatrixFactory = new WrapperMFMatrixFactory();
            wrapperMFMatrixFactory.setMatrixClass(matrixClass);
            innerFactory = wrapperMFMatrixFactory;
        }
    }

    @Override
    public MFMatrix produce() {
        innerFactory.setNumCols(numCols);
        innerFactory.setNumRows(numRows);
        return innerFactory.produce();
    }

}
