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
    protected RawIntegrateResult integrateResult;

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
        logger.info("produced {} integrators", coreNum);
    }

    private int getRunnableNum() {
        return enableMultiThread ? Runtime.getRuntime().availableProcessors() : 1;
    }

    public void process() {
        produceIntegrators();
        executeIntegrators();
        mergyAssemblerResults();
    }

    public IntegrateResult getIntegrateResult() {
        return integrateResult;
    }

    private void executeIntegrators() {
        ExecutorService executor = Executors.newFixedThreadPool(integrators.size());
        for (MFIntegrator runnable : integrators) {
            executor.execute(runnable);
            logger.info("execute {}", runnable);
        }
        logger.info("integrating with {} threads", integrators.size());

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
                logger.info("processed (V,N,D)= {}/{},{}/{},{}/{}",
                        integratorFactory.getVolumeIteratorWrapper().getCount(),
                        integratorFactory.getVolumeIteratorWrapper().getEstimatedSize(),
                        integratorFactory.getNeumannIteratorWrapper().getCount(),
                        integratorFactory.getNeumannIteratorWrapper().getEstimatedSize(),
                        integratorFactory.getDirichletIteratorWrapper().getCount(),
                        integratorFactory.getDirichletIteratorWrapper().getEstimatedSize());
            } catch (InterruptedException ex) {
                logger.error("Processing interrupted {}", ex);
                throw new IllegalStateException(ex);
            }
        }

        integrateResult = new RawIntegrateResult();
        Assembler mainAssemblier = integrators.get(0).getIntegrateCore().getAssembler();
        integrateResult.setGeneralForce(mainAssemblier.getMainVector());
        integrateResult.setMainMatrix(mainAssemblier.getMainMatrix());
        integrateResult.setNodeValueDimension(getNodeValueDimension());
        integrateResult.setUpperSymmetric(mainAssemblier.isUpperSymmetric());

        logger.info("all integrators' mission accomplished");
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