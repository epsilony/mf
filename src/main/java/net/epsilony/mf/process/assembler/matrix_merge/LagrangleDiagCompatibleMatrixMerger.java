/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler.matrix_merge;

import net.epsilony.mf.util.matrix.MFMatrix;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDiagCompatibleMatrixMerger implements MatrixMerger {

    MFMatrix source, destiny;
    int lagrangleSize;

    @Override
    public void setSource(MFMatrix source) {
        this.source = source;
    }

    @Override
    public void setDestiny(MFMatrix destiny) {
        this.destiny = destiny;
    }

    public void setLagrangleSize(int lagrangleSize) {
        this.lagrangleSize = lagrangleSize;
    }

    @Override
    public void merge() {
        SimpMatrixMerger.commonCheck(source, destiny);
        int commonDimension = destiny.numRows() - lagrangleSize;
        for (MatrixEntry me : source) {
            int row = me.row();
            int column = me.column();
            double srcVal = me.get();

            if (srcVal == 0 || row == column && row >= commonDimension) {
                continue;
            }
            destiny.add(row, column, srcVal);
        }
        for (int diag = commonDimension; diag < destiny.numRows(); diag++) {
            if (source.get(diag, diag) == 0) {
                destiny.set(diag, diag, 0);
            }
        }
    }
}
