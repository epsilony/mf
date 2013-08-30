/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFStrainStabilizeIntegrateDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Model2DStrainStabilizeTask extends AbstractModel2DTask implements MFStrainStabilizeIntegrateTask {

    List<MFStrainStabilizeIntegrateDomain> volumeStrainStabilizeDomains;

    @Override
    public List<MFStrainStabilizeIntegrateDomain> volumeDomainTask() {
        return volumeStrainStabilizeDomains;
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends MFStrainStabilizeIntegrateDomain> volumeDomainQuadratures) {
        this.volumeForceFunc = volumnForceFunc;
        this.volumeStrainStabilizeDomains = new ArrayList<>(volumeDomainQuadratures);
    }

    public List<MFStrainStabilizeIntegrateDomain> getVolumeStrainStabilizeDomains() {
        return volumeStrainStabilizeDomains;
    }
}
