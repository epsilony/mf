/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFSolver {

    void setUpperSymmetric(boolean upperSymmetric);

    void setMainMatrix(Matrix mainMatrix);

    void setMainVector(DenseVector mainVector);

    void solve();

    DenseVector getResult();
}
