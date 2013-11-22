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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Aspect
public abstract class AbstractIntegralAspect {

    @Pointcut("target(net.epsilony.mf.process.integrate.MFIntegralProcessor)")
    public void integralProcessorPointcut() {
    };

    @Pointcut("target(net.epsilony.mf.process.integrate.core.MFIntegratorCore)")
    public void integratorCorePointCut() {
    };

    @AfterReturning("integralProcessorPointcut()&&execution(public void setIntegrateUnitsGroup(..))")
    public void afterReturningSetIntegrateUnitsGroup(JoinPoint joinPoint) {
        integralUnitsInjected(joinPoint);
    }

    protected abstract void integralUnitsInjected(JoinPoint joinPoint);

    @Before("integralProcessorPointcut()&&execution(public void integrate())")
    public void beforeSumIntegratePointCut(JoinPoint joinPoint) {
        beforeIntegrate(joinPoint);
    }

    public abstract void beforeIntegrate(JoinPoint joinPoint);

    @AfterReturning("integratorCorePointCut()&&execution(public void integrate())")
    public void integratedAUnitPointCut(JoinPoint joinPoint) {
        integratedAUnit(joinPoint);
    }

    public abstract void integratedAUnit(JoinPoint joinPoint);
}
