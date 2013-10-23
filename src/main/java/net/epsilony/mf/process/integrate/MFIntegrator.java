/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Map;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.Factory;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator {

    void setMixerFactory(Factory<MFMixer> mixerFactory);

    void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroups);

    void setIntegratorCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup);

    void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<MFIntegratePoint>> integrateUnitsGroup);

    void setMainMatrixFactory(Factory<MFMatrix> mainMatrixFactory);

    void setMainVectorFactory(Factory<MFMatrix> mainVectorFactory);

    void integrate();

    MFIntegrateResult getIntegrateResult();
}
