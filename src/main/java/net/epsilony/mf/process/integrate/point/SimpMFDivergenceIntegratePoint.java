/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFDivergenceIntegratePoint extends SimpMFIntegratePoint implements MFDivergenceIntegratePoint {

    double[] unitOutNormal;
    Segment solidBoundary;

    @Override
    public double[] getUnitOutNormal() {
        return unitOutNormal;
    }

    public void setUnitOutNormal(double[] unitOutNormal) {
        this.unitOutNormal = unitOutNormal;
    }

    @Override
    public Segment getSolidBoundary() {
        return solidBoundary;
    }

    public void setSolidBoundary(Segment solidBoundary) {
        this.solidBoundary = solidBoundary;
    }

    
}
