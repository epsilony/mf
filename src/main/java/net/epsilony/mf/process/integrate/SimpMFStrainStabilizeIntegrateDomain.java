/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Iterator;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFStrainStabilizeIntegrateDomain implements MFStrainStabilizeIntegrateDomain {

    List<MFBoundaryIntegratePoint> boundaryIntegratePoints;
    GenericFunction<double[], double[]> loadFunction;

    @Override
    public double[] load(double[] position) {
        return loadFunction.value(position, null);
    }

    @Override
    public Iterator<MFBoundaryIntegratePoint> iterator() {
        return boundaryIntegratePoints.iterator();
    }

    public void setBoundaryIntegratePoints(List<MFBoundaryIntegratePoint> boundaryIntegratePoints) {
        this.boundaryIntegratePoints = boundaryIntegratePoints;
    }

    public void setLoadFunction(GenericFunction<double[], double[]> loadFunction) {
        this.loadFunction = loadFunction;
    }
}
