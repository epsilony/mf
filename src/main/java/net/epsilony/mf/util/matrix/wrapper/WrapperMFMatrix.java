/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MFMatrixData;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WrapperMFMatrix<T> extends MFMatrix {

    T getBackend();
    
    MFMatrixData genMatrixData();
}
