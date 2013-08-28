/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import java.util.Collection;
import java.util.LinkedList;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Model2DTask extends AbstractModelClass implements MFQuadratureTask {
    Collection<? extends QuadraturePoint> volumeQuadraturePoints;

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends QuadraturePoint> quadraturePoints) {
        this.volumeForceFunc = volumnForceFunc;
        volumeQuadraturePoints = quadraturePoints;
    }

    @Override
    public SynchronizedIterator<MFQuadraturePoint<QuadraturePoint>> volumeTasks() {
        LinkedList<MFQuadraturePoint<QuadraturePoint>> res = new LinkedList<>();
        for (QuadraturePoint qp : volumeQuadraturePoints) {
                double[] volForce = volumeForceFunc == null ? null : volumeForceFunc.value(qp.coord, null);
                res.add(new MFQuadraturePoint(qp, volForce, null));
            }
        return new SynchronizedIterator<>(res.iterator(), res.size());
    }
}
