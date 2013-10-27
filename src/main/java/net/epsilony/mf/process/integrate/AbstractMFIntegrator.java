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
public abstract class AbstractMFIntegrator implements MFIntegrator {

    Map<MFProcessType, Assembler> assemblersGroup;
    Map<MFProcessType, MFIntegratorCore> integratorCoresGroup;
    Map<MFProcessType, SynchronizedIterator<MFIntegratePoint>> integrateUnitsGroup;
    Factory<? extends MFMatrix> mainMatrixFactory;
    Factory<? extends MFMatrix> mainVectorFactory;
    Factory<? extends MFMixer> mixerFactory;
    RawMFIntegrateResult integrateResult;

    @Override
    public void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        this.assemblersGroup = assemblersGroup;
    }

    @Override
    public void setIntegratorCoresGroup(Map<MFProcessType, MFIntegratorCore> integratorCoresGroup) {
        this.integratorCoresGroup = integratorCoresGroup;
    }

    @Override
    public void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<MFIntegratePoint>> integrateUnitsGroup) {
        this.integrateUnitsGroup = integrateUnitsGroup;
    }

    public void setMainMatrixFactory(Factory<? extends MFMatrix> mainMatrixFactory) {
        this.mainMatrixFactory = mainMatrixFactory;
    }

    public void setMainVectorFactory(Factory<? extends MFMatrix> mainVectorFactory) {
        this.mainVectorFactory = mainVectorFactory;
    }

    public void setMixerFactory(Factory<? extends MFMixer> mixerFactory) {
        this.mixerFactory = mixerFactory;
    }
}
