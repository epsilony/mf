/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Collection;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.Quadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ModelDomain2DTask extends AbstractModelClass implements MFDomainQuadratureTask {

    Collection<? extends Iterable<MFBoundaryIntegratePoint>> volumeDomainQuadratures;

    @Override
    public List<Quadrature<Segment2DQuadraturePoint>> volumeDomainTask() {
        throw new UnsupportedOperationException();
        //        
//        return new SynchronizedIterator<>(volumeDomainQuadratures.iterator(), volumeDomainQuadratures.size());
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends Iterable<MFBoundaryIntegratePoint>> volumeDomainQuadratures) {
//        this.volumeForceFunc = volumnForceFunc;
//        this.volumeDomainQuadratures = volumeDomainQuadratures;
    }
}
