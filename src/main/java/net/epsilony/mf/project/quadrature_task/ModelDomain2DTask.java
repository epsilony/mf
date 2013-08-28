/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import java.util.Collection;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.Quadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ModelDomain2DTask extends AbstractModelClass implements MFDomainQuadratureTask {

    Collection<? extends Quadrature<Segment2DQuadraturePoint>> volumeDomainQuadratures;

    @Override
    public SynchronizedIterator<Quadrature<Segment2DQuadraturePoint>> volumeDomainTask() {
        
        return new SynchronizedIterator<>(volumeDomainQuadratures.iterator(), volumeDomainQuadratures.size());
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends Quadrature<Segment2DQuadraturePoint>> volumeDomainQuadratures) {
        this.volumeForceFunc = volumnForceFunc;
        this.volumeDomainQuadratures = volumeDomainQuadratures;
    }
}
