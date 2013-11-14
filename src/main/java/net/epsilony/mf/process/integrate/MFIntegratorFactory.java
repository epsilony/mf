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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.core.MFIntegrateCores;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.observer.CounterIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.tb.Factory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegratorFactory implements Factory<MFIntegrator> {

    Integer threadNum = null;
    Map<MFProcessType, MFIntegratorCore> coresGroup = null;
    Set<MFIntegratorObserver> observers = new HashSet<>();
    MatrixFactory<? extends MFMatrix> mainMatrixFactory;
    MatrixFactory<? extends MFMatrix> mainVectorFactory;

    public MFIntegratorFactory() {
        observers.add(new CounterIntegratorObserver());
    }

    public Map<MFProcessType, MFIntegratorCore> getCoresGroup() {
        return coresGroup;
    }

    public void setCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup) {
        this.coresGroup = coresGroup;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    @Override
    public MFIntegrator produce() {
        MFIntegrator integrator = byThreadsNum();
        integrator.setIntegratorCoresGroup(genCoresGroup());
        integrator.addObservers(observers);
        fillMainMatrixVectorFactories(integrator);
        return integrator;
    }

    private MFIntegrator byThreadsNum() {
        if (threadNum == null || threadNum > 1) {
            return new MultithreadMFIntegrator(threadNum);
        } else {
            return new SimpMFIntegrator();
        }
    }

    private Map<MFProcessType, MFIntegratorCore> genCoresGroup() {
        if (coresGroup != null) {
            return coresGroup;
        } else {
            return MFIntegrateCores.commonCoresGroup();
        }
    }

    public boolean addObserver(MFIntegratorObserver e) {
        return observers.add(e);
    }

    private void fillMainMatrixVectorFactories(MFIntegrator integrator) {

        integrator.setMainMatrixFactory(mainMatrixFactory);

        integrator.setMainVectorFactory(mainVectorFactory);
    }

    public void setObservers(Set<MFIntegratorObserver> observers) {
        this.observers = observers;
    }

    public void setMainMatrixFactory(MatrixFactory<? extends MFMatrix> mainMatrixFactory) {
        this.mainMatrixFactory = mainMatrixFactory;
    }

    public void setMainVectorFactory(MatrixFactory<? extends MFMatrix> mainVectorFactory) {
        this.mainVectorFactory = mainVectorFactory;
    }
}
