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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.AutoSparseMatrixFactory;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpDirichletIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpNeumannIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.process.integrate.observer.CounterIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import no.uib.cipr.matrix.DenseMatrix;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class MFIntegratorConf {

    @Bean
    public MFIntegrator mfintegrator() {
        MFIntegratorFactory factory = mfintegratorFactory();

        return factory.produce();
    }

    @Bean
    public MFIntegratorFactory mfintegratorFactory() {
        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setCoresGroup(integratorCoresGroup());
        factory.setMainMatrixFactory(mainMatrixFactory());
        factory.setMainVectorFactory(mainVectorFactory());
        factory.setThreadNum(threadNum());
        factory.setObservers(integratorObservers());
        return factory;
    }

    @Bean
    public Map<MFProcessType, MFIntegratorCore> integratorCoresGroup() {
        EnumMap<MFProcessType, MFIntegratorCore> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new SimpVolumeMFIntegratorCore());
        result.put(MFProcessType.NEUMANN, new SimpNeumannIntegratorCore());
        result.put(MFProcessType.DIRICHLET, new SimpDirichletIntegratorCore());
        return result;
    }

    @Bean
    public MatrixFactory<? extends MFMatrix> mainMatrixFactory() {
        return AutoSparseMatrixFactory.produceDefault();
    }

    @Bean
    public MatrixFactory<? extends MFMatrix> mainVectorFactory() {
        return new AutoMFMatrixFactory(DenseMatrix.class);
    }

    @Bean
    public Integer threadNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Bean
    public Set<MFIntegratorObserver> integratorObservers() {
        return new HashSet<MFIntegratorObserver>(Arrays.asList(new CounterIntegratorObserver()));
    }
}
