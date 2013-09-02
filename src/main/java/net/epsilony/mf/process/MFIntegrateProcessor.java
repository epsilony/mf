/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegrateProcessor {

    public static final Logger logger = LoggerFactory.getLogger(MFIntegrateProcessor.class);
    MFIntegratorFactory integratorFactory = new MFIntegratorFactory();
    private boolean enableMultiThread;
    private List<MFIntegrator> integrators;

    public void setEnableMultiThread(boolean enableMultiThread) {
        this.enableMultiThread = enableMultiThread;
    }

    public void setAssembler(Assembler assembler) {
        integratorFactory.setAssembler(assembler);
    }

    public void setMixerFactory(MFMixerFactory mixerFactory) {
        integratorFactory.setMixerFactory(mixerFactory);
    }

    public void setCore(MFIntegratorCore core) {
        integratorFactory.setCore(core);
    }

    public void setIntegrateTask(MFIntegrateTask task) {
        integratorFactory.setIntegrateTask(task);
    }

    private void produceIntegrators() {
        int coreNum = getRunnableNum();
        integrators = new ArrayList<>(coreNum);
        for (int i = 0; i < coreNum; i++) {
            MFIntegrator runnable = integratorFactory.produce();
            integrators.add(runnable);
        }
    }

    private int getRunnableNum() {
        return enableMultiThread ? Runtime.getRuntime().availableProcessors() : 1;
    }

    public void process() {
        produceIntegrators();
        executeIntegrators();
        mergyAssemblerResults();
    }

    public ProcessResult getProcessResult() {
        SimpProcessResult result = new SimpProcessResult();
        Assembler mainAssemblier = integrators.get(0).getIntegrateCore().getAssembler();
        result.setGeneralForce(mainAssemblier.getMainVector());
        result.setMainMatrix(mainAssemblier.getMainMatrix());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setUpperSymmetric(mainAssemblier.isUpperSymmetric());
        return result;
    }

    private void executeIntegrators() {
        ExecutorService executor = Executors.newFixedThreadPool(integrators.size());
        for (MFIntegrator runnable : integrators) {
            executor.execute(runnable);
            logger.info("execute {}", runnable);
        }
        logger.info("Processing with {} threads", integrators.size());

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException ex) {
                logger.error("Processing interrupted {}", ex);
                break;
            }
        }
    }

    private void mergyAssemblerResults() {
        if (integrators.size() > 1) {
            logger.info("start merging {} assemblers", integrators.size());
            Iterator<MFIntegrator> iter = integrators.iterator();
            Assembler assembler = iter.next().getIntegrateCore().getAssembler();
            int count = 1;
            while (iter.hasNext()) {
                assembler.mergeWithBrother(iter.next().getIntegrateCore().getAssembler());
                count++;
                logger.info("mergied {}/{} assemblers", count, integrators.size());
            }
        }
    }

    public int getNodeValueDimension() {
        return integrators.get(0).getIntegrateCore().getAssembler().getDimension();
    }
}