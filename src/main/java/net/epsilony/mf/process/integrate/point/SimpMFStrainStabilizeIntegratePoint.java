/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.geomodel.MFBoundary;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFStrainStabilizeIntegratePoint extends SimpMFIntegratePoint implements MFStrainStabilizeIntegratePoint {

    double[] unitOutNormal;
    MFBoundary solidBoundary;

    @Override
    public double[] getUnitOutNormal() {
        return unitOutNormal;
    }

    public void setUnitOutNormal(double[] unitOutNormal) {
        this.unitOutNormal = unitOutNormal;
    }

    @Override
    public MFBoundary getSolidBoundary() {
        return solidBoundary;
    }

    public void setSolidBoundary(MFBoundary solidBoundary) {
        this.solidBoundary = solidBoundary;
    }
}
