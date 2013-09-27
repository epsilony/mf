/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFStrainStabilizeIntegratePoint extends RawMFIntegratePoint implements MFStrainStabilizeIntegratePoint {

    double[] unitOutNormal;
    GeomUnit solidBoundary;

    @Override
    public double[] getUnitOutNormal() {
        return unitOutNormal;
    }

    public void setUnitOutNormal(double[] unitOutNormal) {
        this.unitOutNormal = unitOutNormal;
    }

    @Override
    public GeomUnit getSolidBoundary() {
        return solidBoundary;
    }

    public void setSolidBoundary(GeomUnit solidBoundary) {
        this.solidBoundary = solidBoundary;
    }
}
