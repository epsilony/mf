/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.analysis.Dimensional;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFRawIntegratePoint extends Dimensional, IntIdentity {

    double getWeight();

    double[] getCoord();
}
