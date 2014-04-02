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
 * @param <T>
 */
public class SynchronizedMatrixFactory<T> implements MatrixFactory<T> {

    private final MatrixFactory<T> innerFactory;

    public SynchronizedMatrixFactory(MatrixFactory<T> innerFactory) {
        this.innerFactory = innerFactory;
    }

    @Override
    synchronized public void setNumRows(int numRows) {
        innerFactory.setNumRows(numRows);
    }

    @Override
    synchronized public void setNumCols(int numCols) {
        innerFactory.setNumCols(numCols);
    }

    @Override
    synchronized public T get() {
        return innerFactory.get();
    }

    public static <T> MatrixFactory<T> wrap(MatrixFactory<T> factory) {
        return new SynchronizedMatrixFactory<>(factory);
    }
}
