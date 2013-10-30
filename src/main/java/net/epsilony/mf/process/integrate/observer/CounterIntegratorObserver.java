/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import java.util.EnumMap;
import java.util.Map;
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

    MFProcessType currentType = null;
    MFIntegratorStatus currentStatus = null;
    MFIntegrator integrator;
    StringBuilder stringBuilder = new StringBuilder(128);
    long lastLogCount = 0;

    private long timeGapMiliSeconds = 500;

    private boolean firstSwitch = true;

    public CounterIntegratorObserver() {
        for (MFProcessType type : MFProcessType.values()) {
            nums.put(type, new AtomicInteger(-1));
            counts.put(type, new AtomicInteger());
        }
    }

    @Override
    public void update(Map<MFIntegratorObserverKey, Object> data) {
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
    }

    private void finish(Map<MFIntegratorObserverKey, Object> data) {
        logCounts();
        logger.info("finished!");
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
        currentType = (MFProcessType) data.get(MFIntegratorObserverKey.PROCESS_TYPE);
        AtomicInteger num = nums.get(currentType);
        Integer numValue = (Integer) data.get(MFIntegratorObserverKey.INTEGRATE_UNITS_NUM);
        num.set(numValue);
        lastLogCount = System.nanoTime();
        if (!firstSwitch) {
            logCounts();
        }
        firstSwitch = false;
        logger.info("switch to process {}", currentType);
        logCounts();
    }

    private void integratedAnUnit(Map<MFIntegratorObserverKey, Object> data) {
        MFProcessType type = (MFProcessType) data.get(MFIntegratorObserverKey.PROCESS_TYPE);
        if (type != currentType) {
            throw new IllegalStateException();
        }
        counts.get(currentType).incrementAndGet();
        if (isTimeGapFilled()) {
            lastLogCount = System.nanoTime();
            logCounts();
        }
    }

    public long getTimeGapMiliseconds() {
        return timeGapMiliSeconds;
    }

    public void setTimeGapMiliseconds(long timeGapMiliseconds) {
        this.timeGapMiliSeconds = timeGapMiliseconds;
    }

    private boolean isTimeGapFilled() {
        long current = System.nanoTime();
        long gap = current - lastLogCount;
        return gap >= TimeUnit.MILLISECONDS.toNanos(timeGapMiliSeconds);
    }
}
