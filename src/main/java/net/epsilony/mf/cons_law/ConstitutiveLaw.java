/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import java.io.Serializable;
import net.epsilony.tb.analysis.Dimensional;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ConstitutiveLaw extends Dimensional, Serializable {

    DenseMatrix64F getMatrix();

    double[] calcStressByEngineering(double[] engStrain, double[] result);
}
