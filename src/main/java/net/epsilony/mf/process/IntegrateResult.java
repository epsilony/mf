/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface IntegrateResult {

    Matrix getMainMatrix();

    boolean isUpperSymmetric();

    DenseVector getMainVector();

    int getNodeValueDimension();
}
