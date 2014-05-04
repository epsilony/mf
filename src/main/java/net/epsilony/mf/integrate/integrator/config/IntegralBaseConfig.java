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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Resource;

import net.epsilony.mf.integrate.integrator.AssemblerIntegrator;
import net.epsilony.mf.integrate.integrator.GeomPointToShapeFunction;
import net.epsilony.mf.integrate.integrator.GeomQuadraturePointToAssemblyInput;
import net.epsilony.mf.integrate.integrator.GeomQuadraturePointToLagrangleAssemblyInput;
import net.epsilony.mf.integrate.integrator.LoadValueFunction;
import net.epsilony.mf.integrate.integrator.VolumeLoadAssemblerIntegrator;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.SymmetricT2Value;
import net.epsilony.mf.process.assembler.config.AssemblerBaseConfig;
import net.epsilony.mf.process.assembler.config.AssemblersGroup;
import net.epsilony.mf.process.mix.MFMixer;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.util.bus.BiConsumerRegistry;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@Import(CommonToPointsIntegratorConfig.class)
public class IntegralBaseConfig extends ApplicationContextAwareImpl {
    // need to necessities
    public static final String INTEGRAL_COLLECTION_PROTO = "integratorGroupCollectionProto";
    // end

    // optional
    public static final String IS_LAGRANGLE_DIRICHLET = "isLagrangleDirichlete";
    //

    public static final String INTEGRAL_COLLECTIONS = "integratorGroupCollections";
    @Resource(name = ModelBusConfig.LOAD_MAP_BUS)
    BiConsumerRegistry<Map<MFGeomUnit, GeomPointLoad>> loadMapBus;
    public static final String QUADRATURE_DEGREE_BUS = "quadratureDegreeBus";

    @Bean(name = QUADRATURE_DEGREE_BUS)
    public WeakBus<Integer> quadratureDegreeBus() {
        return new WeakBus<>(QUADRATURE_DEGREE_BUS);
    }

    @Bean(name = INTEGRAL_COLLECTIONS)
    public List<Object> integralGroupCollections() {
        return new ArrayList<>();
    }

    public static final String POINT_TO_DIFF_ASM_INPUT_PROTO = "pointToDiffAsmInputProto";

    @Bean(name = POINT_TO_DIFF_ASM_INPUT_PROTO)
    @Scope("prototype")
    public GeomQuadraturePointToAssemblyInput pointToDiffAsmInputProto() {
        GeomQuadraturePointToAssemblyInput result = new GeomQuadraturePointToAssemblyInput();
        result.setLoadValueCalculator(loadValueFunctionProto());
        GeomPointToShapeFunction pointToShapeFunctionValueProto = pointToShapeFunctionValueProto();
        pointToShapeFunctionValueProto.setDiffOrder(1);
        result.setT2ValueCalculator(pointToShapeFunctionValueProto.andThen(SymmetricT2Value::new));
        return result;
    }

    public static final String POINT_TO_ASM_INPUT_PROTO = "pointToAsmInputProto";

    @Bean(name = POINT_TO_ASM_INPUT_PROTO)
    @Scope("prototype")
    public GeomQuadraturePointToAssemblyInput pointToAsmInputProto() {
        GeomQuadraturePointToAssemblyInput result = new GeomQuadraturePointToAssemblyInput();
        result.setLoadValueCalculator(loadValueFunctionProto());
        GeomPointToShapeFunction pointToShapeFunctionValueProto = pointToShapeFunctionValueProto();
        pointToShapeFunctionValueProto.setDiffOrder(0);
        result.setT2ValueCalculator(pointToShapeFunctionValueProto.andThen(SymmetricT2Value::new));
        return result;
    }

    public static final String POINT_TO_SHAPE_FUNCTION_VALUE_PROTO = "pointToShapeFunctionValueProto";

    @Bean(name = POINT_TO_SHAPE_FUNCTION_VALUE_PROTO)
    @Scope("prototype")
    public GeomPointToShapeFunction pointToShapeFunctionValueProto() {
        MFMixer mixer = applicationContext.getBean(MixerConfig.MIXER_PROTO, MFMixer.class);
        GeomPointToShapeFunction result = new GeomPointToShapeFunction(mixer);
        return result;
    }

    public static final String POINT_TO_LAGRANGLE_ASM_INPUT_PROTO = "pointToLagrangleAsmInputProto";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean(name = POINT_TO_LAGRANGLE_ASM_INPUT_PROTO)
    @Scope("prototype")
    public GeomQuadraturePointToLagrangleAssemblyInput pointToLagrangleAsmInputProto() {
        GeomQuadraturePointToLagrangleAssemblyInput result = new GeomQuadraturePointToLagrangleAssemblyInput();
        result.setLoadValueCalculator((Function) loadValueFunctionProto());
        result.setT2ValueCalculator(pointToShapeFunctionValueProto().andThen(SymmetricT2Value::new));
        return result;
    }

    public static final String LOAD_VALUE_FUNCTION_PROTO = "loadValueFunctionProto";

    @Bean(name = LOAD_VALUE_FUNCTION_PROTO)
    @Scope("prototype")
    public Function<GeomPoint, LoadValue> loadValueFunctionProto() {
        LoadValueFunction result = new LoadValueFunction();
        loadMapBus.register(LoadValueFunction::setLoadMap, result);
        return result;
    }

    public static final String ASSEMBLER_INTEGRATOR_GROUP_PROTO = "assemblerIntegratorsGroupProto";

    @Bean(name = ASSEMBLER_INTEGRATOR_GROUP_PROTO)
    @Scope("prototype")
    public ConsumerIntegratorGroup<AssemblyInput> assemblerIntegratorsGroupProto() {
        AssemblersGroup assemblersGroup = applicationContext.getBean(AssemblerBaseConfig.ASSEMBLERS_GROUP_PROTO,
                AssemblersGroup.class);

        VolumeLoadAssemblerIntegrator volume = new VolumeLoadAssemblerIntegrator();
        volume.setVolumeAssembler(assemblersGroup.getVolume());
        volume.setVolumeLoadAssembler(assemblersGroup.getVolumeLoad());

        AssemblerIntegrator neumann;
        if (null == assemblersGroup.getNeumann()) {
            neumann = null;
        } else {
            neumann = new AssemblerIntegrator();
            neumann.setAssembler(assemblersGroup.getNeumann());
        }

        AssemblerIntegrator dirichlet;
        if (assemblersGroup.getDirichlet() == null) {
            dirichlet = null;
        } else {
            dirichlet = new AssemblerIntegrator();
            dirichlet.setAssembler(assemblersGroup.getDirichlet());
        }
        ConsumerIntegratorGroup<AssemblyInput> result = new ConsumerIntegratorGroup<>(volume, neumann, dirichlet);
        return result;
    }

}
