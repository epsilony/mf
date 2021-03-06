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

package net.epsilony.mf.process.assembler.matrix;

import java.math.BigDecimal;
import java.util.Iterator;
import net.epsilony.mf.util.matrix.BigDecimalMFMatrix;
import net.epsilony.mf.util.matrix.BigDecimalMatrixEntry;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BigDecimalLagrangleDiagCompatibleMatrixMerger implements LagrangleMatrixMerger {

    BigDecimalMFMatrix source, destiny;
    int                lagrangleSize;

    @Override
    public void setSource(MFMatrix source) {
        this.source = (BigDecimalMFMatrix) source;
    }

    @Override
    public void setDestiny(MFMatrix destiny) {
        this.destiny = (BigDecimalMFMatrix) destiny;
    }

    public void setSource(BigDecimalMFMatrix source) {
        this.source = source;
    }

    public void setDestiny(BigDecimalMFMatrix destiny) {
        this.destiny = destiny;
    }

    @Override
    public void merge() {
        SimpMatrixMerger.commonCheck(source, destiny);
        int commonDimension = destiny.numRows() - lagrangleSize;
        Iterator<BigDecimalMatrixEntry> bigDecimalIterator = source.bigDecimalIterator();
        while (bigDecimalIterator.hasNext()) {
            BigDecimalMatrixEntry me = bigDecimalIterator.next();
            int row = me.row();
            int column = me.column();
            BigDecimal srcVal = me.get();

            if (srcVal.compareTo(BigDecimal.ZERO) == 0 || row == column && row >= commonDimension) {
                continue;
            }
            destiny.add(row, column, srcVal);
        }
        for (int diag = commonDimension; diag < destiny.numRows(); diag++) {
            if (source.getBigDecimal(diag, diag).compareTo(BigDecimal.ZERO) == 0) {
                destiny.set(diag, diag, BigDecimal.ZERO);
            }
        }
    }

    @Override
    public void setLagrangleSize(int lagrangleSize) {
        this.lagrangleSize = lagrangleSize;
    }

}
