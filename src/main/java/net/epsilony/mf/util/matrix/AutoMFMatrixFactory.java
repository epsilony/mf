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

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrixFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AutoMFMatrixFactory implements MatrixFactory<MFMatrix> {

    int                               numRows;
    int                               numCols;
    Class<?>                          matrixClass;
    MatrixFactory<? extends MFMatrix> innerFactory;

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

    @SuppressWarnings("unchecked")
    private void _setMatrixClass(Class<?> matrixClass) {
        this.matrixClass = matrixClass;
        if (MFMatrix.class.isAssignableFrom(matrixClass)) {
            SimpMFMatrixFactory<MFMatrix> simpMFMatrixFactory = new SimpMFMatrixFactory<MFMatrix>();
            simpMFMatrixFactory.setMatrixClass((Class<MFMatrix>) matrixClass);
            innerFactory = simpMFMatrixFactory;
        } else {
            WrapperMFMatrixFactory wrapperMFMatrixFactory = new WrapperMFMatrixFactory();
            wrapperMFMatrixFactory.setMatrixClass(matrixClass);
            innerFactory = wrapperMFMatrixFactory;
        }
    }

    @Override
    public MFMatrix get() {
        innerFactory.setNumCols(numCols);
        innerFactory.setNumRows(numRows);
        return innerFactory.get();
    }

}
