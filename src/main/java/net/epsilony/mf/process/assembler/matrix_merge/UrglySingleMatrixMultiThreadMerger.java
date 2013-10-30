/* (c) Copyright by Man YUAN */
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
