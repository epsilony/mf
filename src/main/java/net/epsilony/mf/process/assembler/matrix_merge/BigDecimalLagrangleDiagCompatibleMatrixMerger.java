/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler.matrix_merge;

import java.math.BigDecimal;
import java.util.Iterator;
import net.epsilony.mf.util.matrix.BigDecimalMFMatrix;
import net.epsilony.mf.util.matrix.BigDecimalMatrixEntry;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BigDecimalLagrangleDiagCompatibleMatrixMerger implements MatrixMerger {

    BigDecimalMFMatrix source, destiny;
    int lagrangleSize;

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

    public void setLagrangleSize(int lagrangleSize) {
        this.lagrangleSize = lagrangleSize;
    }

}
