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

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SingleSynchronziedBigDecimalMatrixFactory implements MatrixFactory<SynchronizedBigDecimalMFMatrix> {

    int numRows = -1;
    int numCols = -1;
    SynchronizedBigDecimalMFMatrix matrix = null;
    Class<? extends BigDecimalMFMatrix> matrixClass;

    public SingleSynchronziedBigDecimalMatrixFactory(Class<? extends BigDecimalMFMatrix> matrixClass) {
        this.matrixClass = matrixClass;
    }

    @Override
    public void setNumRows(int numRows) {
        if (matrix == null) {
            this.numRows = numRows;
        } else {
            if (this.numRows != numRows) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public void setNumCols(int numCols) {
        if (matrix == null) {
            this.numCols = numCols;
        } else {
            if (this.numCols != numCols) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public SynchronizedBigDecimalMFMatrix get() {
        if (matrix == null) {
            genMatrix();
        }
        return matrix;
    }

    private void genMatrix() {
        AutoMFMatrixFactory autoMFMatrixFactory = new AutoMFMatrixFactory(matrixClass);
        autoMFMatrixFactory.setNumCols(numCols);
        autoMFMatrixFactory.setNumRows(numRows);
        BigDecimalMFMatrix bigDecimalMFMatrix = (BigDecimalMFMatrix) autoMFMatrixFactory.get();
        matrix = new SynchronizedBigDecimalMFMatrix(bigDecimalMFMatrix);
    }
}
