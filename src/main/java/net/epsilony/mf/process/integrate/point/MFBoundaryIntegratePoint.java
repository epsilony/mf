/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.geomodel.MFBoundary;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFBoundaryIntegratePoint extends MFIntegratePoint {

    MFBoundary getBoundary();

    double getBoundaryParameter();

    double[] getOutNormal();
}
