/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.process.integrate.point.MFDivergenceIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegrateDomain extends Iterable<MFDivergenceIntegratePoint> {

    double[] load(double[] position);
}
