/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawConstitutiveLaw implements ConstitutiveLaw {

    DenseMatrix matrix;

    @Override
    public DenseMatrix getMatrix() {
        return matrix;
    }

    public RawConstitutiveLaw(DenseMatrix matrix) {
        this.matrix = matrix;
    }
}
