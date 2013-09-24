/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.SimpMFIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Common2DTask extends Abstract2DTask implements MFIntegrateTask {

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
            SimpMFIntegratePoint pt = new SimpMFIntegratePoint();
            pt.setCoord(qp.coord);
            pt.setWeight(qp.weight);
            pt.setLoad(volForce);
            res.add(pt);
        }
        return res;
    }
}
