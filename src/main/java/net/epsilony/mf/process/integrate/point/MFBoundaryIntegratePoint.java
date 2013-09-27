/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFBoundaryIntegratePoint extends MFIntegratePoint {

    GeomUnit getBoundary();

    double getBoundaryParameter();

    double[] getOutNormal();   //for MFNode only
}
