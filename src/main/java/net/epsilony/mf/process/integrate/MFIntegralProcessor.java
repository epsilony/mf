/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.assembler.matrix_merge.BigDecimalLagrangleDiagCompatibleMatrixMerger;
import net.epsilony.mf.process.assembler.matrix_merge.LagrangleDiagCompatibleMatrixMerger;
import net.epsilony.mf.process.assembler.matrix_merge.LagrangleMatrixMerger;
import net.epsilony.mf.process.assembler.matrix_merge.MatrixMerger;
import net.epsilony.mf.process.assembler.matrix_merge.SimpBigDecimalMatrixMerger;
import net.epsilony.mf.process.assembler.matrix_merge.SimpMatrixMerger;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.mf.util.MFUtils;
import net.epsilony.mf.util.matrix.BigDecimalMFMatrix;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.synchron.SynchronizedIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegralProcessor {

    MatrixFactory<? extends MFMatrix> mainMatrixFactory;
    MatrixFactory<? extends MFMatrix> mainVectorFactory;
    int mainMatrixSize;
    Factory<MFIntegrator> integratorFactory;
    List<MFIntegrator> integrators;
    Factory<? extends MFMixer> mixerFactory;
    RawMFIntegrateResult integrateResult;
    int threadNum;
    int integralDegree;

    Logger logger = LoggerFactory.getLogger(MFIntegralProcessor.class);
    MatrixMerger mainVectorMerger;
    MatrixMerger mainMatrixMerger;
    Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> integrateUnitsGroup;
    Factory<Map<AssemblerType, Assembler>> assemblerFactory;
    Map<GeomUnit, MFLoad> loadMap;

    public void integrate() {
        integrateResult = null;
        prepareIntegrators();
        executeIntegrators();
        mergeSubIntegrateResults();
    }

    private void prepareIntegrators() {
        mainMatrixFactory.setNumCols(mainMatrixSize);
        mainMatrixFactory.setNumRows(mainMatrixSize);
        mainVectorFactory.setNumCols(1);
        mainVectorFactory.setNumRows(mainMatrixSize);
        integrators = new ArrayList<>(threadNum);
        Map<GeomUnit, LockableHolder<MFLoad>> lockableLoadMap = MFUtils.lockablyWrapValues(loadMap);
        for (int i = 0; i < threadNum; i++) {
            MFIntegrator integrator = integratorFactory.produce();
            integrator.setIntegralDegree(integralDegree);
            integrator.setMainMatrix(mainMatrixFactory.produce());
            integrator.setMainVector(mainVectorFactory.produce());
            integrator.setAssemblersGroup(assemblerFactory.produce());
            integrator.setIntegrateUnitsGroup(integrateUnitsGroup);
            integrator.setMixer(mixerFactory.produce());
            integrator.setLoadMap(new HashMap<>(lockableLoadMap));
            integrators.add(integrator);
        }
        logger.info("prepared {} integrators", threadNum);
    }

    private void executeIntegrators() {
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        ArrayList<Future<?>> futures = new ArrayList<>(threadNum);
        for (MFIntegrator subIntegrator : integrators) {
            Future<?> future = executor.submit(new IntegrateRunnable(subIntegrator));
            futures.add(future);
        }
        executor.shutdown();
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("sub integrator execution error");
                throw new IllegalStateException(ex);
            }
        }
        logger.info("all sub-integrators' missions accomplished");
    }

    private void mergeSubIntegrateResults() {

        Iterator<MFIntegrator> subIntegratorsIter = integrators.iterator();
        MFIntegrator firstIntegrator = subIntegratorsIter.next();
        MFIntegrateResult firstIntegrateResult = firstIntegrator.getIntegrateResult();

        integrateResult = new RawMFIntegrateResult();
        integrateResult.set(firstIntegrateResult);

        if (integrators.size() == 1) {
            return;
        }
        logger.info("start merging {} sub-integrators' work", integrators.size());
        int count = 1;
        if (null == mainMatrixMerger) {
            mainMatrixMerger = defaultMainMatrixMerger();
        }
        if (null == mainVectorMerger) {
            mainVectorMerger = defaultMainVectorMerger();
        }
        if (integrateResult.isLagrangle()) {
            LagrangleMatrixMerger lagrangleMerger = (LagrangleMatrixMerger) mainMatrixMerger;
            lagrangleMerger.setLagrangleSize(integrateResult.getLagrangleDimension());
        }
        mainMatrixMerger.setDestiny(integrateResult.mainMatrix);
        mainVectorMerger.setDestiny(integrateResult.mainVector);
        do {
            MFIntegrator subIntegrator = subIntegratorsIter.next();
            MFIntegrateResult subIntegrateResult = subIntegrator.getIntegrateResult();

            if (subIntegrateResult.isLagrangle() != integrateResult.isLagrangle()
                    || subIntegrateResult.getLagrangleDimension() != integrateResult.getLagrangleDimension()) {
                throw new IllegalStateException();
            }

            mainMatrixMerger.setSource(subIntegrateResult.getMainMatrix());
            mainMatrixMerger.merge();
            mainVectorMerger.setSource(subIntegrateResult.getMainVector());
            mainVectorMerger.merge();
            count++;
            logger.info("mergied {}/{} assemblers", count, integrators.size());

        } while (subIntegratorsIter.hasNext());
    }

    public MFIntegrateResult getIntegrateResult() {
        return integrateResult;
    }

    public void setMainVectorMerger(MatrixMerger mainVectorMerger) {
        this.mainVectorMerger = mainVectorMerger;
    }

    public void setMainMatrixMerger(MatrixMerger mainMatrixMerger) {
        this.mainMatrixMerger = mainMatrixMerger;
    }

    private MatrixMerger defaultMainMatrixMerger() {
        if (integrateResult.getMainMatrix() instanceof BigDecimalMFMatrix) {
            if (integrateResult.isLagrangle()) {
                BigDecimalLagrangleDiagCompatibleMatrixMerger merger = new BigDecimalLagrangleDiagCompatibleMatrixMerger();
                return merger;
            } else {
                return new SimpBigDecimalMatrixMerger();
            }
        } else if (integrateResult.isLagrangle()) {
            LagrangleDiagCompatibleMatrixMerger merger = new LagrangleDiagCompatibleMatrixMerger();
            return merger;
        } else {
            return new SimpMatrixMerger();
        }
    }

    private MatrixMerger defaultMainVectorMerger() {
        if (integrateResult.mainVector instanceof BigDecimalMFMatrix) {
            return new SimpBigDecimalMatrixMerger();
        } else {
            return new SimpMatrixMerger();
        }
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

    public void setIntegralDegree(int integralDegree) {
        this.integralDegree = integralDegree;
    }

    public void setMainMatrixFactory(MatrixFactory<? extends MFMatrix> mainMatrixFactory) {
        this.mainMatrixFactory = mainMatrixFactory;
    }

    public void setMainVectorFactory(MatrixFactory<? extends MFMatrix> mainVectorFactory) {
        this.mainVectorFactory = mainVectorFactory;
    }

    public void setMainMatrixSize(int mainMatrixSize) {
        this.mainMatrixSize = mainMatrixSize;
    }

    public void setIntegratorFactory(Factory<MFIntegrator> integratorFactory) {
        this.integratorFactory = integratorFactory;
    }

    public void setMixerFactory(Factory<? extends MFMixer> mixerFactory) {
        this.mixerFactory = mixerFactory;
    }

    public void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> integrateUnitsGroup) {
        this.integrateUnitsGroup = integrateUnitsGroup;
    }

    public void setAssemblersGroupList(Factory<Map<AssemblerType, Assembler>> assemblerFactory) {
        this.assemblerFactory = assemblerFactory;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public void setLoadMap(Map<GeomUnit, MFLoad> loadMap) {
        this.loadMap = loadMap;
    }
}
