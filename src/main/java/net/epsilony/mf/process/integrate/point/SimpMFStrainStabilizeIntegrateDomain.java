/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import java.util.Iterator;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFStrainStabilizeIntegrateDomain implements MFStrainStabilizeIntegrateDomain {

    List<MFStrainStabilizeIntegratePoint> divergenceIntegratePoints;
    GenericFunction<double[], double[]> loadFunction;

    @Override
    public double[] load(double[] position) {
        return loadFunction.value(position, null);
    }

    @Override
    public Iterator<MFStrainStabilizeIntegratePoint> iterator() {
        return divergenceIntegratePoints.iterator();
    }

    public void setBoundaryIntegratePoints(List<MFStrainStabilizeIntegratePoint> divergenceIntegratePoints) {
        this.divergenceIntegratePoints = divergenceIntegratePoints;
    }

    public void setLoadFunction(GenericFunction<double[], double[]> loadFunction) {
        this.loadFunction = loadFunction;
    }
}
