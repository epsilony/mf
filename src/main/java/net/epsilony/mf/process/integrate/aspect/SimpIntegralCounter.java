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
package net.epsilony.mf.process.integrate.aspect;

import java.util.List;
import java.util.Map;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.tb.synchron.SynchronizedIterator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */

@Aspect
public class SimpIntegralCounter {

    public static final Logger logger = LoggerFactory.getLogger(SimpIntegralCounter.class);
    int threadNum;
    long lastCountLogTime = 0;
    long countLogGap = 500000000;

    private int volCount = 0;
    private int volSize;
    private int neuCount = 0;
    private int neuSize;
    private int diriCount = 0;
    private int diriSize;

    @Pointcut("target(net.epsilony.mf.process.integrate.MFIntegralProcessor)")
    public void integralProcessorPointcut() {
    };

    @Pointcut("target(net.epsilony.mf.process.integrate.core.MFIntegratorCore)")
    public void integratorCorePointCut() {
    };

    @SuppressWarnings("unchecked")
    @AfterReturning("integralProcessorPointcut()&&execution(public void setIntegrators(..))")
    public void integratorsInjected(JoinPoint joinPoint) {
        List<MFIntegrator> integrators = (List<MFIntegrator>) joinPoint.getArgs()[0];
        threadNum = integrators.size();
    }

    @AfterReturning("integralProcessorPointcut()&&execution(public void setIntegrateUnitsGroup(..))")
    @SuppressWarnings("unchecked")
    public void integralUnitsInjected(JoinPoint joinPoint) {
        Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> unitesGroup = (Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>>) joinPoint
                .getArgs()[0];
        volSize = unitesGroup.get(MFProcessType.VOLUME).getEstimatedSize();
        neuSize = unitesGroup.get(MFProcessType.NEUMANN).getEstimatedSize();
        diriSize = unitesGroup.get(MFProcessType.DIRICHLET).getEstimatedSize();
    }

    @Before("integralProcessorPointcut()&&execution(public void integrate())")
    public void beforeIntegrate(JoinPoint joinPoint) {
        logger.info("with {} integral threads", threadNum);
        logger.info("integral units numbers (V, N, D) = ({}, {}, {})", volSize, neuSize, diriSize);
    }

    @AfterReturning("integratorCorePointCut()&&execution(public void integrate())")
    synchronized public void integratedAUnit(JoinPoint joinPoint) {
        MFIntegratorCore integratorCore = (MFIntegratorCore) joinPoint.getTarget();
        switch (integratorCore.getProcessType()) {
        case VOLUME:
            volCount++;
            break;
        case DIRICHLET:
            diriCount++;
            break;
        case NEUMANN:
            neuCount++;
            break;
        default:
            return;
        }
        logCounts();
    }

    private void logCounts() {
        long gap = System.nanoTime() - lastCountLogTime;
        if (gap < countLogGap && (volCount < volSize || neuCount < neuSize || diriCount < diriSize)) {
            return;
        }

        logger.info("integrated (V, N, D) : {}/{}, {}/{}, {}/{}", volCount, volSize, neuCount, neuSize, diriCount,
                diriSize);
        lastCountLogTime = System.nanoTime();
    }

}
