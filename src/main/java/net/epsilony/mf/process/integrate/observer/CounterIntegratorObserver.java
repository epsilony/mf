/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.epsilony.mf.process.MFProcessType;
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

    StringBuilder stringBuilder = new StringBuilder(128);
    long lastTimeLoggingCounts = 0;

    private long timeGapMiliSeconds = 500;

    public CounterIntegratorObserver() {
        for (MFProcessType type : MFProcessType.values()) {
            nums.put(type, new AtomicInteger(-1));
            counts.put(type, new AtomicInteger());
        }
    }

    @Override
    synchronized public void update(Map<MFIntegratorObserverKey, Object> data) {
        MFIntegratorStatus status = (MFIntegratorStatus) data.get(MFIntegratorObserverKey.STATUS);
        switch (status) {
            case STARTED:
                started(data);
                break;
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
    }

    private void started(Map<MFIntegratorObserverKey, Object> data) {
        logger.info("{} started", data.get(MFIntegratorObserverKey.INTEGRATOR));
    }

    private void finish(Map<MFIntegratorObserverKey, Object> data) {
        logCounts();
        logger.info("{} finished!", data.get(MFIntegratorObserverKey.INTEGRATOR));
    }

    private void logCounts() {
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
            }
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        logger.info(stringBuilder.toString());
    }

    private void typeSwitched(Map<MFIntegratorObserverKey, Object> data) {
        MFProcessType type = (MFProcessType) data.get(MFIntegratorObserverKey.PROCESS_TYPE);
        Object integrator = data.get(MFIntegratorObserverKey.INTEGRATOR);
        AtomicInteger num = nums.get(type);
        Integer numValue = (Integer) data.get(MFIntegratorObserverKey.INTEGRATE_UNITS_NUM);
        num.set(numValue);
        logger.info("{} switch to process {}", integrator, type);
    }

    private void integratedAnUnit(Map<MFIntegratorObserverKey, Object> data) {
        MFProcessType type = (MFProcessType) data.get(MFIntegratorObserverKey.PROCESS_TYPE);
        counts.get(type).incrementAndGet();
        if (isTimeGapFilled()) {
            lastTimeLoggingCounts = System.nanoTime();
            logCounts();
        }
    }

    synchronized public long getTimeGapMiliseconds() {
        return timeGapMiliSeconds;
    }

    synchronized public void setTimeGapMiliseconds(long timeGapMiliseconds) {
        this.timeGapMiliSeconds = timeGapMiliseconds;
    }

    private boolean isTimeGapFilled() {
        long current = System.nanoTime();
        long gap = current - lastTimeLoggingCounts;
        return gap >= TimeUnit.MILLISECONDS.toNanos(timeGapMiliSeconds);
    }
}
