/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.unit;

import net.epsilony.mf.process.integrate.unit.MFStrainStabilizeIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegrateDomain extends Iterable<MFStrainStabilizeIntegratePoint> {

    double[] load(double[] position);
}
