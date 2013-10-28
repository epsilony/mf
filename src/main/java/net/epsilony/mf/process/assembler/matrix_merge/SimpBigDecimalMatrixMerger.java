/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler.matrix_merge;

import java.util.Iterator;
import net.epsilony.mf.util.matrix.BigDecimalMFMatrix;
import net.epsilony.mf.util.matrix.BigDecimalMatrixEntry;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpBigDecimalMatrixMerger implements MatrixMerger {

    BigDecimalMFMatrix source, destiny;

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
        Iterator<BigDecimalMatrixEntry> bigDecimalIterator = source.bigDecimalIterator();
        while (bigDecimalIterator.hasNext()) {
            BigDecimalMatrixEntry me = bigDecimalIterator.next();
            destiny.add(me.row(), me.column(), me.get());
        }
    }
}
