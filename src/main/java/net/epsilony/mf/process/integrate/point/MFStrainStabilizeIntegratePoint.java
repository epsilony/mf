/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.geomodel.MFBoundary;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegratePoint extends MFIntegratePoint {

    double[] getUnitOutNormal();

    MFBoundary getSolidBoundary();
}
