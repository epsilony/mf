/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import java.util.LinkedList;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.RawMatrixEntry;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWrapperMFMatrix<T> implements WrapperMFMatrix<T> {

    protected T matrix;

    @Override
    public T getBackend() {
        return matrix;
    }

    @Override
    public MFMatrixData genMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(numCols());
        data.setNumRows(numRows());
        LinkedList<MatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : this) {
            entries.add(new RawMatrixEntry(me));
        }
        data.setMatrixEntries(entries);
        data.setMatrixClass(matrix.getClass());
        return data;
    }
}
