/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.epsilony.mf.process.MFProcessType.DIRICHLET;
import static net.epsilony.mf.process.MFProcessType.NEUMANN;
import static net.epsilony.mf.process.MFProcessType.VOLUME;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorCore;
import net.epsilony.mf.process.integrate.SimpDirichletIntegratorCore;
import net.epsilony.mf.process.integrate.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.process.integrate.SimpMFIntegrator;
import net.epsilony.mf.process.integrate.SimpNeumannIntegratorCore;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegratorFactory {

    Assembler assembler;
    MFMixerFactory mixerFactory;
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

    public void setIntegrateTask(MFIntegrateTask task) {
        List volumeTasks = task.volumeTasks();
        volumeIteratorWrapper = new SynchronizedIterator(volumeTasks.iterator(), volumeTasks.size());
        List neumannTasks = task.neumannTasks();
        neumannIteratorWrapper = new SynchronizedIterator(neumannTasks.iterator(), neumannTasks.size());
        List dirichletTasks = task.dirichletTasks();
        dirichletIteratorWrapper = new SynchronizedIterator(dirichletTasks.iterator(), dirichletTasks.size());
    }

    public Map<String, Object> produce() {
        Map<String, Object> result = new HashMap<>();
        Assembler asmClone = SerializationUtils.clone(assembler);
        asmClone.prepare();
        result.put(Assembler.class.getSimpleName(), asmClone);

        asmClone.prepare();
        MFMixer mixer = mixerFactory.produce();

        for (MFProcessType type : MFProcessType.values()) {
            MFIntegrator integrator = new SimpMFIntegrator();
            MFIntegratorCore core = genDefaultCore(type);
            integrator.setIntegrateCore(core);
            core.setAssembler(asmClone);
            core.setMixer(mixer);

            integrator.setIntegrateUnits(getIntegrateUnits(type));

            result.put(type.name(), integrator);
        }
        return result;
    }

    private MFIntegratorCore genDefaultCore(MFProcessType type) {
        switch (type) {
            case VOLUME:
                return new SimpVolumeMFIntegratorCore();
            case NEUMANN:
                return new SimpNeumannIntegratorCore();
            case DIRICHLET:
                return new SimpDirichletIntegratorCore();
            default:
                throw new IllegalStateException();
        }
    }

    private SynchronizedIterator getIntegrateUnits(MFProcessType type) {
        switch (type) {
            case VOLUME:
                return volumeIteratorWrapper;
            case NEUMANN:
                return neumannIteratorWrapper;
            case DIRICHLET:
                return dirichletIteratorWrapper;
            default:
                throw new IllegalStateException();
        }
    }
}
