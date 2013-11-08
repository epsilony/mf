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

package net.epsilony.mf.process.assembler.matrix_merge;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class UrglySingleMatrixMultiThreadMerger implements LagrangleMatrixMerger {

    int lagrangleSize;
    MFMatrix source;
    MFMatrix destiny;
    boolean merged = false;

    @Override
    public void setSource(MFMatrix source) {
        this.source = source;
    }

    @Override
    public void setDestiny(MFMatrix destiny) {
        this.destiny = destiny;
    }

    @Override
    public void merge() {
        if (destiny != source) {
            throw new IllegalArgumentException();
        }
        if (merged) {
            return;
        }
        for (int col = source.numCols() - lagrangleSize; col < source.numCols(); col++) {
            boolean isOne = true;
            for (int row = 0; row < col; row++) {
                if (source.get(row, col) != 0) {
                    isOne = false;
                    break;
                }
            }
            source.set(col, col, isOne ? 1 : 0);
        }
        merged = true;
    }

    @Override
    public void setLagrangleSize(int lagrangleSize) {
        this.lagrangleSize = lagrangleSize;
    }

}
