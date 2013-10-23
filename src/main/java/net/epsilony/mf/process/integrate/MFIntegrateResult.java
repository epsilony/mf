/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateResult {

    boolean isLagrangle();

    int getLagrangleDimension();

    MFMatrix getMainMatrix();

    MFMatrix getMainVector();
}
