/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.List;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorCore;
import net.epsilony.mf.process.integrate.SimpMFIntegrateCore;
import net.epsilony.mf.process.integrate.SimpMFIntegrator;
import net.epsilony.tb.Factory;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegratorFactory implements Factory<MFIntegrator> {

    Assembler assembler;
    MFMixerFactory mixerFactory;
    MFIntegratorCore core = new SimpMFIntegrateCore();
    SynchronizedIterator volumeIteratorWrapper, dirichletIteratorWrapper, neumannIteratorWrapper;

    public SynchronizedIterator getVolumeIteratorWrapper() {
        return volumeIteratorWrapper;
    }

    public SynchronizedIterator getDirichletIteratorWrapper() {
        return dirichletIteratorWrapper;
    }

    public SynchronizedIterator getNeumannIteratorWrapper() {
        return neumannIteratorWrapper;
    }

    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    public void setMixerFactory(MFMixerFactory mixerFactory) {
        this.mixerFactory = mixerFactory;
    }

    public void setCore(MFIntegratorCore core) {
        this.core = core;
    }

    public void setIntegrateTask(MFIntegrateTask task) {
        List volumeTasks = task.volumeTasks();
        volumeIteratorWrapper = new SynchronizedIterator(volumeTasks.iterator(), volumeTasks.size());
        List neumannTasks = task.neumannTasks();
        neumannIteratorWrapper = new SynchronizedIterator(neumannTasks.iterator(), neumannTasks.size());
        List dirichletTasks = task.dirichletTasks();
        dirichletIteratorWrapper = new SynchronizedIterator(dirichletTasks.iterator(), dirichletTasks.size());
    }

    @Override
    public MFIntegrator produce() {

        Assembler asmClone = SerializationUtils.clone(assembler);
        asmClone.prepare();
        MFMixer mixer = mixerFactory.produce();
        MFIntegrator runnable = new SimpMFIntegrator();
        MFIntegratorCore coreClone = SerializationUtils.clone(core);
        runnable.setIntegrateCore(coreClone);
        coreClone.setAssembler(asmClone);
        coreClone.setMixer(mixer);
        runnable.setVolumeIterator(volumeIteratorWrapper);
        runnable.setDirichletIterator(dirichletIteratorWrapper);
        runnable.setNeumannIterator(neumannIteratorWrapper);
        return runnable;
    }
}
