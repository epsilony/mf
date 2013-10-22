/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.MFIntegrator;
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
    private List<Map<String, Object>> integratorGroups;
    protected RawIntegrateResult integrateResult;
    Integer forcibleThreadNum;

    public Integer getForcibleThreadNum() {
        return forcibleThreadNum;
    }

    public void setForcibleThreadNum(Integer forcibleThreadNum) {
        if (null != forcibleThreadNum) {
            if (forcibleThreadNum < 1) {
                throw new IllegalArgumentException("forcible thread num should be null or >= 1, not " + forcibleThreadNum);
            }
        }
        this.forcibleThreadNum = forcibleThreadNum;
    }

    public void setEnableMultiThread(boolean enableMultiThread) {
        this.enableMultiThread = enableMultiThread;
    }

    public void setAssembler(Assembler assembler) {
        integratorFactory.setAssembler(assembler);
    }

    public void setMixerFactory(MFMixerFactory mixerFactory) {
        integratorFactory.setMixerFactory(mixerFactory);
    }

    public void setIntegrateTask(MFIntegrateTask task) {
        integratorFactory.setIntegrateTask(task);
    }

    private void produceIntegrators() {
        int coreNum = getRunnableNum();
        integratorGroups = new ArrayList<>(coreNum);
        for (int i = 0; i < coreNum; i++) {
            Map<String, Object> group = integratorFactory.produce();
            integratorGroups.add(group);
        }
        logger.info("produced {} integrators", coreNum);
    }

    private int getRunnableNum() {
        if (!enableMultiThread) {
            return 1;
        }
        if (null == forcibleThreadNum) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return forcibleThreadNum;
        }
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
        ExecutorService executor = Executors.newFixedThreadPool(integratorGroups.size());
        for (Map<String, Object> group : integratorGroups) {
            executor.execute(new IntegrateRunnable(group));
            logger.info("execute {}", group);
        }
        logger.info("integrating with {} threads", integratorGroups.size());

        executor.shutdown();
        waitTillExecutorFinished(executor);

        integrateResult = new RawIntegrateResult();
        Assembler mainAssemblier = (Assembler) integratorGroups.get(0).get(Assembler.class.getSimpleName());
        integrateResult.setGeneralForce(mainAssemblier.getMainVector());
        integrateResult.setMainMatrix(mainAssemblier.getMainMatrix());
        integrateResult.setNodeValueDimension(getNodeValueDimension());
        integrateResult.setUpperSymmetric(mainAssemblier.isUpperSymmetric());

        logger.info("all integrators' mission accomplished");
    }

    private void waitTillExecutorFinished(ExecutorService executor) {
        int lastVolume = -1;
        int lastNeumann = -1;
        int lastDirichlet = -1;
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MILLISECONDS);

                int vol = integratorFactory.getVolumeIteratorWrapper().getCount();
                int neu = integratorFactory.getNeumannIteratorWrapper().getCount();
                int diri = integratorFactory.getDirichletIteratorWrapper().getCount();
                boolean needLog = false;
                if (vol != lastVolume) {
                    lastVolume = vol;
                    needLog = true;
                }
                if (neu != lastNeumann) {
                    lastNeumann = neu;
                    needLog = true;
                }
                if (diri != lastDirichlet) {
                    lastDirichlet = diri;
                    needLog = true;
                }
                if (!needLog) {
                    continue;
                }
                logger.info("processed (V,N,D)= {}/{},{}/{},{}/{}",
                        vol,
                        integratorFactory.getVolumeIteratorWrapper().getEstimatedSize(),
                        neu,
                        integratorFactory.getNeumannIteratorWrapper().getEstimatedSize(),
                        diri,
                        integratorFactory.getDirichletIteratorWrapper().getEstimatedSize());
            } catch (InterruptedException ex) {
                logger.error("Processing interrupted {}", ex);
                throw new IllegalStateException(ex);
            }
        }
    }

    private static class IntegrateRunnable implements Runnable {

        Map<String, Object> integratorGroup;

        public IntegrateRunnable(Map<String, Object> integratorGroup) {
            this.integratorGroup = integratorGroup;
        }

        @Override
        public void run() {
            for (MFProcessType type : MFProcessType.values()) {
                MFIntegrator integrator = (MFIntegrator) integratorGroup.get(type.toString());
                integrator.integrate();
            }
        }
    }

    private void mergyAssemblerResults() {
        if (integratorGroups.size() > 1) {
            logger.info("start merging {} assemblers", integratorGroups.size());
            Iterator<Map<String, Object>> iter = integratorGroups.iterator();
            Assembler assembler = (Assembler) iter.next().get(Assembler.class.getSimpleName());
            int count = 1;
            while (iter.hasNext()) {
                assembler.mergeWithBrother((Assembler) iter.next().get(Assembler.class.getSimpleName()));
                count++;
                logger.info("mergied {}/{} assemblers", count, integratorGroups.size());
            }
        }
    }

    public int getNodeValueDimension() {
        Assembler assembler = (Assembler) integratorGroups.get(0).get(Assembler.class.getSimpleName());
        return assembler.getValueDimension();
    }
}