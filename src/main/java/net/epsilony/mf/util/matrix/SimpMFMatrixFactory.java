/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
public class SimpMFMatrixFactory<T extends MFMatrix> implements MatrixFactory<T> {

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
    public T get() {
        try {
            T result = constructor.newInstance(numRows, numCols);
            return result;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SimpMFMatrixFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new IllegalStateException();
    }
}
