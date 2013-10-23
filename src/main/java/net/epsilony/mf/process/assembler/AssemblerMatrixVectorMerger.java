/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.util.matrix.MFMatrix;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AssemblerMatrixVectorMerger {

    MFMatrix sourceMainMatrix, sourceMainVector, destinyMainMatrix, destinyMainVector;
    boolean lagrangle = false;
    int lagrangleDimension;

    public void setSourceMainMatrix(MFMatrix sourceMainMatrix) {
        this.sourceMainMatrix = sourceMainMatrix;
    }

    public void setSourceMainVector(MFMatrix sourceMainVector) {
        this.sourceMainVector = sourceMainVector;
    }

    public void setDestinyMainMatrix(MFMatrix destinyMainMatrix) {
        this.destinyMainMatrix = destinyMainMatrix;
    }

    public void setDestinyMainVector(MFMatrix destinyMainVector) {
        this.destinyMainVector = destinyMainVector;
    }

    public void setLagrangle(boolean lagrangle) {
        this.lagrangle = lagrangle;
    }

    public void setLagrangleDimension(int lagrangleDimension) {
        this.lagrangleDimension = lagrangleDimension;
    }

    public void merge() {
        mergeMainVector();
        mergeMainMatrix();
    }

    private void mergeMainVector() {
        for (MatrixEntry me : sourceMainVector) {
            destinyMainVector.add(me.row(), me.column(), me.get());
        }
    }

    private void mergeMainMatrix() {
        if (lagrangle) {
            mergeAccordingToLagrangleDiagConvention();
        } else {
            commonMerge();
        }

    }

    private void mergeAccordingToLagrangleDiagConvention() {
        int commonDimension = destinyMainMatrix.numRows() - lagrangleDimension;
        for (MatrixEntry me : sourceMainMatrix) {
            int row = me.row();
            int column = me.column();
            double srcVal = me.get();

            if (srcVal == 0 || row == column && row >= commonDimension) {
                continue;
            }
            destinyMainMatrix.add(row, column, srcVal);
        }
        for (int diag = commonDimension; diag < destinyMainMatrix.numRows(); diag++) {
            if (sourceMainMatrix.get(diag, diag) == 0) {
                destinyMainMatrix.set(diag, diag, 0);
            }
        }
    }

    private void commonMerge() {
        for (MatrixEntry me : sourceMainMatrix) {
            destinyMainMatrix.add(me.row(), me.column(), me.get());
        }
    }
}
