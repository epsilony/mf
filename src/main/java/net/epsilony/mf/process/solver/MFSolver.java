/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFSolver {

    void setMainMatrix(MFMatrix mainMatrix);

    void setMainVector(MFMatrix mainVector);

    void solve();

    MFMatrix getResult();
}
