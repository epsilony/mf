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

import java.util.EnumMap;
import java.util.Map;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.AutoSparseMatrixFactory;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpDirichletIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpNeumannIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.tb.Factory;
import no.uib.cipr.matrix.DenseMatrix;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class MFIntegralProcessorConf {

    @Bean
    public MFIntegralProcessor mfintegralProcessor() {
        MFIntegralProcessor processor = new MFIntegralProcessor();
        processor.setMainMatrixFactory(mainMatrixFactory());
        processor.setMainVectorFactory(mainVectorFactory());
        processor.setIntegratorFactory(mfintegratorFactory());
        processor.setThreadNum(threadNum());
        processor.setIntegralDegree(integralDegree());
        return processor;
    }

    @Bean
    public Factory<MFIntegrator> mfintegratorFactory() {
        return new Factory<MFIntegrator>() {

            @Override
            public MFIntegrator produce() {
                return mfintegrator();
            }
        };
    }

    @Bean
    @Scope("prototype")
    public MFIntegrator mfintegrator() {
        SimpMFIntegrator integrator = new SimpMFIntegrator();
        integrator.setIntegratorCoresGroup(integratorCoresGroup());
        return integrator;
    }

    @Bean
    @Scope("prototype")
    public Map<MFProcessType, MFIntegratorCore> integratorCoresGroup() {
        EnumMap<MFProcessType, MFIntegratorCore> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, volumeIntegratorCore());
        result.put(MFProcessType.NEUMANN, neumannIntegratorCore());
        result.put(MFProcessType.DIRICHLET, dirichletIntegratorCore());
        return result;
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore volumeIntegratorCore() {
        return new SimpVolumeMFIntegratorCore();
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore neumannIntegratorCore() {
        return new SimpNeumannIntegratorCore();
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore dirichletIntegratorCore() {
        return new SimpDirichletIntegratorCore();
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
    public int integralDegree() {
        return 2;
    }
}
