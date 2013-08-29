/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Model2DTask extends AbstractModelClass implements MFIntegrateTask {

    Collection<? extends QuadraturePoint> volumeQuadraturePoints;

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends QuadraturePoint> quadraturePoints) {
        this.volumeForceFunc = volumnForceFunc;
        volumeQuadraturePoints = quadraturePoints;
    }

    @Override
    public List<MFIntegratePoint> volumeTasks() {
        LinkedList<MFIntegratePoint> res = new LinkedList<>();
        for (QuadraturePoint qp : volumeQuadraturePoints) {
            double[] volForce = volumeForceFunc == null ? null : volumeForceFunc.value(qp.coord, null);
            res.add(new SimpMFIntegratePoint(qp, volForce));
        }
        return res;
    }
}
