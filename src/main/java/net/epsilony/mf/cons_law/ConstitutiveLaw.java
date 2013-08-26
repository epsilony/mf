/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import net.epsilony.tb.analysis.Dimensional;
import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ConstitutiveLaw extends Dimensional {

    DenseMatrix getMatrix();

    double[] calcStressByEngineering(double[] engStrain, double[] result);
}
