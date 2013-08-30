/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFBoundaryIntegratePoint extends MFIntegratePoint {

    Segment getBoundary();

    double getBoundaryParameter();
}
