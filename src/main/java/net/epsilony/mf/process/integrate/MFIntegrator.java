/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Collection;
import java.util.Map;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.util.MFObservable;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.tb.Factory;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator extends MFObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> {

    void setMixerFactory(Factory<? extends MFMixer> mixerFactory);

    void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroups);

    void setIntegratorCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup);

    void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<MFIntegratePoint>> integrateUnitsGroup);

    void setMainMatrixFactory(MatrixFactory<? extends MFMatrix> mainMatrixFactory);

    void setMainVectorFactory(MatrixFactory<? extends MFMatrix> mainVectorFactory);

    void setMainMatrixSize(int mainMatrixSize);

    void integrate();

    MFIntegrateResult getIntegrateResult();
}
