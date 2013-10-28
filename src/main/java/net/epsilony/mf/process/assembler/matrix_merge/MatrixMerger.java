/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler.matrix_merge;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MatrixMerger {

    void setSource(MFMatrix source);

    void setDestiny(MFMatrix destiny);

    void merge();
}
