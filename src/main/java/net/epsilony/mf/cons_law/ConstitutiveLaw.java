/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import java.io.Serializable;
import net.epsilony.tb.analysis.Dimensional;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ConstitutiveLaw extends Dimensional, Serializable {

    double[] calcStressByEngineeringStrain(double[] engStrain, double[] result);
}
