/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.MFIntegrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class CounterIntegratorObserver implements MFIntegratorObserver {

    private final Logger logger = LoggerFactory.getLogger(CounterIntegratorObserver.class);
    private final EnumMap<MFProcessType, AtomicInteger> nums = new EnumMap<>(MFProcessType.class);
    private final EnumMap<MFProcessType, AtomicInteger> counts = new EnumMap<>(MFProcessType.class);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    MFProcessType currentType = null;
    MFIntegratorStatus currentStatus = null;
    MFIntegrator integrator;

    private long timeGapMiliSeconds = 500;

    public CounterIntegratorObserver() {
        for (MFProcessType type : MFProcessType.values()) {
            nums.put(type, new AtomicInteger(-1));
            counts.put(type, new AtomicInteger());
        }
        executorService.submit(new InnerRunnable());
        executorService.shutdown();
    }

    @Override
    public void update(Map<MFIntegratorObserverKey, Object> data) {
        synchronized (this) {
            currentStatus = (MFIntegratorStatus) data.get(MFIntegratorObserverKey.STATUS);
            switch (currentStatus) {
                case FINISHED:
                    finish(data);
                    break;
                case PROCESS_TYPE_SWITCHTED:
                    typeSwitched(data);
                    break;
                case AN_UNIT_IS_INTEGRATED:
                    integratedAnUnit(data);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            goadLoggingThread();
        }
    }

    private void finish(Map<MFIntegratorObserverKey, Object> data) {
    }

    private void typeSwitched(Map<MFIntegratorObserverKey, Object> data) {
        currentType = (MFProcessType) data.get(MFIntegratorObserverKey.PROCESS_TYPE);
        AtomicInteger num = nums.get(currentType);
        Integer numValue = (Integer) data.get(MFIntegratorObserverKey.INTEGRATE_UNITS_NUM);
        num.set(numValue);
    }

    private void integratedAnUnit(Map<MFIntegratorObserverKey, Object> data) {
        MFProcessType type = (MFProcessType) data.get(MFIntegratorObserverKey.PROCESS_TYPE);
        if (type != currentType) {
            throw new IllegalStateException();
        }
        counts.get(currentType).incrementAndGet();
    }

    synchronized private void goadLoggingThread() {
        notifyAll();
    }

    synchronized public long getTimeGapMiliseconds() {
        return timeGapMiliSeconds;
    }

    synchronized public void setTimeGapMiliseconds(long timeGapMiliseconds) {
        this.timeGapMiliSeconds = timeGapMiliseconds;
    }

    private class InnerRunnable implements Runnable {

        EnumMap<MFProcessType, Integer> oldCounts = new EnumMap<>(MFProcessType.class);
        EnumMap<MFProcessType, Integer> oldNums = new EnumMap<>(MFProcessType.class);
        StringBuilder stringBuilder = new StringBuilder(128);
        long last = 0;

        public InnerRunnable() {
            for (MFProcessType type : MFProcessType.values()) {
                oldCounts.put(type, 0);
                oldNums.put(type, -1);
            }
        }

        @Override
        public void run() {
            synchronized (CounterIntegratorObserver.this) {
                do {
                    try {
                        CounterIntegratorObserver.this.wait();
                    } catch (InterruptedException ex) {
                        throw new IllegalStateException(ex);
                    }
                    if (isTimeGapFilled()) {
                        logCounts();
                    }
                } while (!Thread.interrupted() && currentStatus != MFIntegratorStatus.FINISHED);
                logFinished();
            }
        }

        private boolean isTimeGapFilled() {
            long current = System.nanoTime();
            long gap = current - last;
            last = current;
            return gap >= TimeUnit.MILLISECONDS.toNanos(timeGapMiliSeconds);
        }

        private void logCounts() {
            if (!isChanged()) {
                return;
            }
            stringBuilder.setLength(0);
            stringBuilder.append("V, N, D = ");
            for (MFProcessType type : MFProcessType.values()) {
                int num = nums.get(type).get();
                if (num < 0) {
                    stringBuilder.append("_/_, ");
                } else {
                    int count = counts.get(type).get();
                    stringBuilder.append(count);
                    stringBuilder.append("/");
                    stringBuilder.append(num);
                    stringBuilder.append(", ");
                    oldNums.put(type, num);
                    oldCounts.put(type, count);
                }
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            logger.info(stringBuilder.toString());
        }

        private void logFinished() {
            logCounts();
            logger.info("finished!");
        }

        private boolean isChanged() {
            for (MFProcessType type : MFProcessType.values()) {
                int num = nums.get(type).get();
                Integer oldNum = oldNums.get(type);
                if (oldNum != num) {
                    return true;
                }
                int count = counts.get(type).get();
                Integer oldCount = oldCounts.get(type);
                if (count != oldCount) {
                    return true;
                }
            }
            return false;
        }
    }

}
