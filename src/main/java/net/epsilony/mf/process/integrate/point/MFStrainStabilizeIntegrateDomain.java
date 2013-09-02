/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.process.integrate.point.MFStrainStabilizeIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegrateDomain extends Iterable<MFStrainStabilizeIntegratePoint> {

    double[] load(double[] position);
}
