/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegrateDomain extends Iterable<MFBoundaryIntegratePoint> {

    double[] load(double[] position);
}
