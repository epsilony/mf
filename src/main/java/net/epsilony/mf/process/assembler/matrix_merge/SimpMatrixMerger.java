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
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMatrixMerger implements MatrixMerger {

    MFMatrix source, destiny;

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
        check();
        for (MatrixEntry me : source) {
            destiny.add(me.row(), me.column(), me.get());
        }
    }

    private void check() {
        commonCheck(source, destiny);
    }

    public static void commonCheck(MFMatrix source, MFMatrix destiny) {
        if (source.numCols() != destiny.numCols() || source.numRows() != destiny.numRows()) {
            throw new IllegalStateException("size mismatch!");
        }
        if (source == destiny) {
            throw new IllegalStateException("source and destiny cann't be the same");
        }
    }
}
