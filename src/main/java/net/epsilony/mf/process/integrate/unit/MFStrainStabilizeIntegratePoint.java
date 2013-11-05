/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.unit;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegratePoint extends MFIntegratePoint {

    double[] getUnitOutNormal();

    GeomUnit getSolidBoundary();
}
