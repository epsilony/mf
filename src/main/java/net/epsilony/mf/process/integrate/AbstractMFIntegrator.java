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
    Factory<MFMatrix> mainMatrixFactory;
    Factory<MFMatrix> mainVectorFactory;
    Factory<MFMixer> mixerFactory;
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

    @Override
    public void setMainMatrixFactory(Factory<MFMatrix> mainMatrixFactory) {
        this.mainMatrixFactory = mainMatrixFactory;
    }

    @Override
    public void setMainVectorFactory(Factory<MFMatrix> mainVectorFactory) {
        this.mainVectorFactory = mainVectorFactory;
    }

    @Override
    public void setMixerFactory(Factory<MFMixer> mixerFactory) {
        this.mixerFactory = mixerFactory;
    }
}
