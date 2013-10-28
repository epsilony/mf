/* (c) Copyright by Man YUAN */
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
