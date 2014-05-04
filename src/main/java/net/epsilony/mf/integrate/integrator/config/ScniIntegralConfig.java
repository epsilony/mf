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
package net.epsilony.mf.integrate.integrator.config;

import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamOneOne;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.integrator.ScniPolygonToAssemblyInput;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.mix.MFMixer;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.util.function.TypeMapFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class ScniIntegralConfig extends IntegralBaseConfig {

    @Bean(name = IntegralBaseConfig.INTEGRAL_COLLECTION_PROTO)
    @Scope("prototype")
    public ScniIntegralCollection scniIntegralCollectionProto() {
        ScniPolygonToAssemblyInput scniVolumeUnitToAssemblyInputsProto = scniVolumeUnitToAssemblyInputsProto();
        @SuppressWarnings("unchecked")
        TypeMapFunction<Object, Collection<? extends GeomQuadraturePoint>> commonToPointsIntegartor = (TypeMapFunction<Object, Collection<? extends GeomQuadraturePoint>>) applicationContext
                .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);
        Function<Object, Stream<AssemblyInput>> neumannToAsm = oneStreamOneOne(
                commonToPointsIntegartor.andThen(Collection::stream), pointToAsmInputProto());
        Function<Object, Stream<AssemblyInput>> dirichletToAsm;
        if (applicationContext.containsBean(IS_LAGRANGLE_DIRICHLET)
                && !applicationContext.getBean(IS_LAGRANGLE_DIRICHLET, Boolean.class)) {
            dirichletToAsm = oneStreamOneOne(commonToPointsIntegartor.andThen(Collection::stream),
                    pointToAsmInputProto());
        } else {
            dirichletToAsm = oneStreamOneOne(commonToPointsIntegartor.andThen(Collection::stream),
                    pointToLagrangleAsmInputProto());
        }
        @SuppressWarnings({ "rawtypes", "unchecked" })
        FunctionIntegratorGroup<Object, Stream<AssemblyInput>> toAsmGroup = new FunctionIntegratorGroup<Object, Stream<AssemblyInput>>(
                (Function) scniVolumeUnitToAssemblyInputsProto.andThen(Stream::of), neumannToAsm, dirichletToAsm);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        FunctionIntegratorGroup<Object, Stream<GeomQuadraturePoint>> mediaGroup = new FunctionIntegratorGroup<Object, Stream<GeomQuadraturePoint>>(
                (Function) scniVolumeUnitToAssemblyInputsProto.mediaIntegrator(),
                (Function) commonToPointsIntegartor.andThen(Collection::stream),
                (Function) commonToPointsIntegartor.andThen(Collection::stream));

        ScniIntegralCollection result = new ScniIntegralCollection(toAsmGroup, mediaGroup,
                assemblerIntegratorsGroupProto());
        integralGroupCollections().add(result);
        return result;
    }

    public static final String SCNI_VOLUME_UNIT_TO_ASSEMBLY_INPUTS_PROTO = "scniVolumeUnitToAssemblyInputsProto";

    @Bean(name = SCNI_VOLUME_UNIT_TO_ASSEMBLY_INPUTS_PROTO)
    @Scope("prototype")
    public ScniPolygonToAssemblyInput scniVolumeUnitToAssemblyInputsProto() {
        ScniPolygonToAssemblyInput result = new ScniPolygonToAssemblyInput();
        result.setLoadValueFunction(loadValueFunctionProto());
        result.setMixer(applicationContext.getBean(MixerConfig.MIXER_PROTO, MFMixer.class));
        quadratureDegreeBus().register(ScniPolygonToAssemblyInput::setQuadratureDegree, result);
        return result;
    }
}
