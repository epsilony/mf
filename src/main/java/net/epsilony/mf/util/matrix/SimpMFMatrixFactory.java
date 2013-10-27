/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 */
public class SimpMFMatrixFactory<T extends MFMatrix> implements MFMatrixFactory<T> {

    int numRows;
    int numCols;
    Class<T> matrixClass;
    private Constructor<T> constructor;

    @Override
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    @Override
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public void setMatrixClass(Class<T> matrixClass) {
        this.matrixClass = matrixClass;
        try {
            constructor = matrixClass.getConstructor(int.class, int.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public T produce() {
        try {
            T result = constructor.newInstance(numRows, numCols);
            return result;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SimpMFMatrixFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new IllegalStateException();
    }
}
