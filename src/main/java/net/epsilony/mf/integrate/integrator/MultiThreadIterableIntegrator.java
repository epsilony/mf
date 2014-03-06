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
package net.epsilony.mf.integrate.integrator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.epsilony.tb.synchron.SynchronizedIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MultiThreadIterableIntegrator<IN> extends AbstractIntegrator<Iterable<? extends IN>> {
    List<? extends Integrator<IN>> subIntegrators;
    Logger logger = LoggerFactory.getLogger(MultiThreadIterableIntegrator.class);
    SynchronizedIterator<IN> synchronizedIterator;
    int integratedCount = 0;

    // mainly designed for AOP
    synchronized public void integrated(IN unit) {
        integratedCount++;
    }

    public synchronized int getIntegratedCount() {
        return integratedCount;
    }

    @Override
    public void integrate() {
        integratedCount = 0;
        synchronizedIterator = new SynchronizedIterator<IN>(unit.iterator());
        ExecutorService executor = Executors.newFixedThreadPool(subIntegrators.size());
        List<Future<?>> futures = new ArrayList<>(subIntegrators.size());
        for (Integrator<IN> subIntegrator : subIntegrators) {
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

    private class IntegrateRunnable implements Runnable {

        Integrator<IN> integrator;

        public IntegrateRunnable(Integrator<IN> integrator) {
            this.integrator = integrator;
        }

        @Override
        public void run() {
            while (true) {
                IN nextItem = synchronizedIterator.nextItem();
                if (null == nextItem) {
                    break;
                }
                integrator.setIntegrateUnit(nextItem);
                integrator.integrate();
                integrated(nextItem);
            }
        }
    }

    public void setSubIntegrators(List<? extends Integrator<IN>> subIntegrators) {
        this.subIntegrators = subIntegrators;
    }

}
