/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerMatrixVectorMerger;
import net.epsilony.mf.util.SynchronizedFactoryWrapper;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.Factory;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MultithreadMFIntegrator extends AbstractMFIntegrator {

    private boolean enableMultiThread = true;
    Integer forcibleThreadNum;
    ArrayList<MFIntegrator> subIntegrators;
    Factory<MFMixer> synchronizedMixerFactory;
    Factory<MFMatrix> synchronizedMainMatrixFactory;
    Factory<MFMatrix> synchronizedMainVectorFactory;
    Logger logger = LoggerFactory.getLogger(MultithreadMFIntegrator.class);

    @Override
    public void integrate() {
        integrateResult = null;
        produceIntegrators();
        executeIntegrators();
        mergeSubIntegrateResults();
    }

    private void produceIntegrators() {
        int threadsNum = getThreadsNum();
        subIntegrators = new ArrayList<>(threadsNum);
        for (int i = 0; i < threadsNum; i++) {
            subIntegrators.add(genSubIntegrator());
        }
        logger.info("produced {} sub-integrators", threadsNum);
    }

    private void executeIntegrators() {
        ExecutorService executor = Executors.newFixedThreadPool(subIntegrators.size());
        for (MFIntegrator subIntegrator : subIntegrators) {
            executor.execute(new IntegrateRunnable(subIntegrator));
            logger.info("execute {}", subIntegrator);
        }
        executor.shutdown();
        logger.info("integrating with {} threads", subIntegrators.size());
        waitTillExecutorFinished(executor);
        logger.info("all sub-integrators' missions accomplished");
    }

    private void mergeSubIntegrateResults() {

        Iterator<MFIntegrator> subIntegratorsIter = subIntegrators.iterator();
        MFIntegrator firstIntegrator = subIntegratorsIter.next();
        MFIntegrateResult firstIntegrateResult = firstIntegrator.getIntegrateResult();

        integrateResult = new RawMFIntegrateResult();
        integrateResult.set(firstIntegrateResult);

        if (subIntegrators.size() == 1) {
            return;
        }
        logger.info("start merging {} sub-integrators' work", subIntegrators.size());
        int count = 1;
        AssemblerMatrixVectorMerger merger = new AssemblerMatrixVectorMerger();
        merger.setDestinyMainMatrix(integrateResult.mainMatrix);
        merger.setDestinyMainVector(integrateResult.mainVector);
        merger.setLagrangle(firstIntegrateResult.isLagrangle());
        merger.setLagrangleDimension(firstIntegrateResult.getLagrangleDimension());
        do {
            MFIntegrator subIntegrator = subIntegratorsIter.next();
            MFIntegrateResult subIntegrateResult = subIntegrator.getIntegrateResult();

            if (subIntegrateResult.isLagrangle() != integrateResult.isLagrangle() || subIntegrateResult.getLagrangleDimension() != integrateResult.getLagrangleDimension()) {
                throw new IllegalStateException();
            }

            merger.setSourceMainMatrix(subIntegrateResult.getMainMatrix());
            merger.setSourceMainVector(subIntegrateResult.getMainVector());
            merger.merge();
            count++;
            logger.info("mergied {}/{} assemblers", count, subIntegrators.size());

        } while (subIntegratorsIter.hasNext());
    }

    @Override
    public MFIntegrateResult getIntegrateResult() {
        return integrateResult;
    }

    private static class IntegrateRunnable implements Runnable {

        MFIntegrator integrator;

        public IntegrateRunnable(MFIntegrator integrator) {
            this.integrator = integrator;
        }

        @Override
        public void run() {
            integrator.integrate();
        }
    }

    private void waitTillExecutorFinished(ExecutorService executor) {
        int lastVolume = -1;
        int lastNeumann = -1;
        int lastDirichlet = -1;
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MILLISECONDS);

                int vol = integrateUnitsGroup.get(MFProcessType.VOLUME).getCount();
                int neu = integrateUnitsGroup.get(MFProcessType.NEUMANN).getCount();
                int diri = integrateUnitsGroup.get(MFProcessType.DIRICHLET).getCount();
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
                        integrateUnitsGroup.get(MFProcessType.VOLUME).getEstimatedSize(),
                        neu,
                        integrateUnitsGroup.get(MFProcessType.NEUMANN).getEstimatedSize(),
                        diri,
                        integrateUnitsGroup.get(MFProcessType.DIRICHLET).getEstimatedSize());
            } catch (InterruptedException ex) {
                logger.error("Processing interrupted {}", ex);
                throw new IllegalStateException(ex);
            }
        }
    }

    private int getThreadsNum() {
        if (!enableMultiThread) {
            return 1;
        }
        if (null == forcibleThreadNum) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return forcibleThreadNum;
        }
    }

    private MFIntegrator genSubIntegrator() {
        SimpMFIntegrator simpMFIntegrator = new SimpMFIntegrator();
        simpMFIntegrator.setIntegrateUnitsGroup(integrateUnitsGroup);
        simpMFIntegrator.setAssemblersGroup(cloneAssemblersGroup());
        simpMFIntegrator.setIntegratorCoresGroup(cloneIntegratorCoresGroup());
        simpMFIntegrator.setMixerFactory(synchronizedMixerFactory);
        simpMFIntegrator.setMainMatrixFactory(synchronizedMainMatrixFactory);
        simpMFIntegrator.setMainVectorFactory(synchronizedMainVectorFactory);
        return simpMFIntegrator;
    }

    private Map<MFProcessType, Assembler> cloneAssemblersGroup() {
        return cloneMapWithSameKeyAndClonedValue(assemblersGroup);
    }

    private Map<MFProcessType, MFIntegratorCore> cloneIntegratorCoresGroup() {
        integratorCoresGroup = MFIntegrateCores.commonCoresGroup();//todo: 
        return cloneMapWithSameKeyAndClonedValue(integratorCoresGroup);
    }

    private static <K extends Enum<K>, V extends Serializable> Map<K, V> cloneMapWithSameKeyAndClonedValue(Map<K, V> src) {
        EnumMap result = new EnumMap(MFProcessType.class);
        for (Map.Entry<K, V> entry : src.entrySet()) {
            result.put(entry.getKey(), SerializationUtils.clone(entry.getValue()));
        }
        return result;
    }

    @Override
    public void setMainMatrixFactory(Factory<MFMatrix> mainMatrixFactory) {
        super.setMainMatrixFactory(mainMatrixFactory);
        synchronizedMainMatrixFactory = new SynchronizedFactoryWrapper<>(mainMatrixFactory);
    }

    @Override
    public void setMainVectorFactory(Factory<MFMatrix> mainVectorFactory) {
        super.setMainVectorFactory(mainVectorFactory);
        synchronizedMainVectorFactory = new SynchronizedFactoryWrapper<>(mainVectorFactory);
    }

    @Override
    public void setMixerFactory(Factory<MFMixer> mixerFactory) {
        super.setMixerFactory(mixerFactory);
        synchronizedMixerFactory = new SynchronizedFactoryWrapper<>(mixerFactory);
    }

    public void setEnableMultiThread(boolean enableMultiThread) {
        this.enableMultiThread = enableMultiThread;
    }

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
}
