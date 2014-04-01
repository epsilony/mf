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

import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamConsumer;
import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamOneOne;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Resource;

import net.epsilony.mf.integrate.integrator.AssemblerIntegrator;
import net.epsilony.mf.integrate.integrator.GeomPointToShapeFunction;
import net.epsilony.mf.integrate.integrator.GeomQuadraturePointToAssemblyInput;
import net.epsilony.mf.integrate.integrator.GeomQuadraturePointToLagrangleAssemblyInput;
import net.epsilony.mf.integrate.integrator.LineToGeomQuadraturePoints;
import net.epsilony.mf.integrate.integrator.LoadValueFunction;
import net.epsilony.mf.integrate.integrator.NodeToGeomQuadraturePoints;
import net.epsilony.mf.integrate.integrator.PolygonToGeomQuadraturePoints;
import net.epsilony.mf.integrate.integrator.VolumeLoadAssemblerIntegrator;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MixerConfig;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.SymmetricT2Value;
import net.epsilony.mf.process.assembler.config.AssemblerBaseConfig;
import net.epsilony.mf.process.assembler.config.AssemblersGroup;
import net.epsilony.mf.util.bus.ConsumerBus;
import net.epsilony.mf.util.bus.ConsumerRegistry;
import net.epsilony.mf.util.function.TypeMapFunction;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class IntegratorBaseConfig extends ApplicationContextAwareImpl {

    // need for config
    public static final String INTEGRATORS_GROUP_PROTO = "integratorsGroupProto";
    // end of need

    public static final String INTEGRATORS_GROUPS = "integratorsGroups";

    @Bean(name = INTEGRATORS_GROUPS)
    public List<IntegratorsGroup> integratorsGroups() {
        return new ArrayList<>();
    }

    @Resource(name = ModelBusConfig.LOAD_MAP_BUS)
    ConsumerRegistry<Map<GeomUnit, GeomPointLoad>> loadMapBus;

    public static final String LAGRANGLE_INTEGRATORS_GROUP_PROTO = "lagrangleIntegratorsGroupProto";

    @Bean(name = LAGRANGLE_INTEGRATORS_GROUP_PROTO)
    @Scope("prototype")
    public IntegratorsGroup lagrangleIntegratorsGroupProto() {
        return buildIntegratorsGroupProto(pointToLagrangleAsmInputProto());
    }

    public static final String NON_LAGRANGLE_INTEGRATORS_GROUP_PROTO = "nonLagrangleIntegratorsGroupProto";

    @Bean(name = NON_LAGRANGLE_INTEGRATORS_GROUP_PROTO)
    @Scope("prototype")
    public IntegratorsGroup nonLagrangleIntegratorsGroupProto() {
        return buildIntegratorsGroupProto(pointToAsmInputProto());
    }

    private IntegratorsGroup buildIntegratorsGroupProto(
            Function<? super GeomQuadraturePoint, ? extends AssemblyInput> dirichletPointToAssemblyInput) {

        Function<Object, Collection<? extends GeomQuadraturePoint>> commonUnitToPointsProto = commonUnitToPointsProto();
        Function<Object, Stream<? extends GeomQuadraturePoint>> commonUnitToPointsStreamProto = commonUnitToPointsProto
                .andThen(Collection::stream);
        GeomQuadraturePointToAssemblyInput pointToAsmInputProto = pointToAsmInputProto();
        GeomQuadraturePointToAssemblyInput pointToDiffAsmInputProto = pointToDiffAsmInputProto();
        AssemblerIntegratorsGroup assemblerIntegratorsGroupProto = assemblerIntegratorsGroupProto();

        IntegratorsGroup result = new IntegratorsGroup();

        // volume
        Function<Object, Stream<AssemblyInput>> volumeT = oneStreamOneOne(commonUnitToPointsStreamProto,
                pointToDiffAsmInputProto);
        Consumer<Object> volume = oneStreamConsumer(volumeT, assemblerIntegratorsGroupProto.getVolume());
        result.setVolume(volume);
        result.setVolumeStack(Arrays.asList(commonUnitToPointsProto, pointToDiffAsmInputProto,
                assemblerIntegratorsGroupProto.getVolume()));

        // neumann
        Function<Object, Stream<AssemblyInput>> neumannT = oneStreamOneOne(commonUnitToPointsStreamProto,
                pointToAsmInputProto);
        Consumer<Object> neumann = oneStreamConsumer(neumannT, assemblerIntegratorsGroupProto.getNeumann());
        result.setNeumann(neumann);
        result.setNeumannStack(Arrays.asList(commonUnitToPointsProto, pointToAsmInputProto,
                assemblerIntegratorsGroupProto.getNeumann()));

        // dirichlet
        Function<Object, Stream<AssemblyInput>> dirichletT = oneStreamOneOne(commonUnitToPointsStreamProto,
                dirichletPointToAssemblyInput);
        Consumer<Object> dirichlet = oneStreamConsumer(dirichletT, assemblerIntegratorsGroupProto.getDirichlet());
        result.setDirichlet(dirichlet);
        result.setDirichletStack(Arrays.asList(commonUnitToPointsProto, dirichletPointToAssemblyInput,
                assemblerIntegratorsGroupProto.getDirichlet()));

        //
        integratorsGroups().add(result);
        return result;

    }

    public static final String COMMON_UNIT_TO_POINTS_PROTO = "commonUnitToPointsProto";

    @Bean(name = COMMON_UNIT_TO_POINTS_PROTO)
    @Scope("prototype")
    Function<Object, Collection<? extends GeomQuadraturePoint>> commonUnitToPointsProto() {
        TypeMapFunction<Object, Collection<? extends GeomQuadraturePoint>> typeMapFunction = new TypeMapFunction<>();
        typeMapFunction.register(PolygonIntegrateUnit.class, polygonToPointsProto());
        typeMapFunction.register(Line.class, lineToPointsProto());
        typeMapFunction.register(Node.class, new NodeToGeomQuadraturePoints());
        typeMapFunction.register(GeomQuadraturePoint.class, Function.identity());
        return typeMapFunction;
    }

    public static final String QUADRATURE_DEGREE_BUS = "quadratureDegreeBus";

    @Bean(name = QUADRATURE_DEGREE_BUS)
    public ConsumerBus<Integer> quadratureDegreeBus() {
        return new ConsumerBus<>();
    }

    public static final String POLYGON_TO_POINTS_PROTO = "polygonToPointsProto";

    @Bean(name = POLYGON_TO_POINTS_PROTO)
    @Scope("prototype")
    public Function<PolygonIntegrateUnit, List<GeomQuadraturePoint>> polygonToPointsProto() {
        PolygonToGeomQuadraturePoints polygonToGeomQuadraturePoints = new PolygonToGeomQuadraturePoints();
        quadratureDegreeBus().register(polygonToGeomQuadraturePoints::setDegree);
        return polygonToGeomQuadraturePoints;
    }

    public static final String LINE_TO_POINTS_PROTO = "lineToPointsProto";

    @Bean(name = LINE_TO_POINTS_PROTO)
    @Scope("prototype")
    public Function<Line, List<GeomQuadraturePoint>> lineToPointsProto() {
        LineToGeomQuadraturePoints lineToGeomQuadraturePoints = new LineToGeomQuadraturePoints();
        quadratureDegreeBus().register(lineToGeomQuadraturePoints::setQuadratureDegree);
        return lineToGeomQuadraturePoints;
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
        loadMapBus.register(result::setLoadMap);
        return result;
    }

    public static final String ASSEMBLER_INTEGRATOR_GROUP_PROTO = "assemblerIntegratorsGroupProto";

    @Bean(name = ASSEMBLER_INTEGRATOR_GROUP_PROTO)
    @Scope("prototype")
    public AssemblerIntegratorsGroup assemblerIntegratorsGroupProto() {
        AssemblersGroup assemblersGroup = applicationContext.getBean(AssemblerBaseConfig.ASSEMBLERS_GROUP_PROTO,
                AssemblersGroup.class);

        VolumeLoadAssemblerIntegrator volume = new VolumeLoadAssemblerIntegrator();
        volume.setVolumeAssembler(assemblersGroup.getVolume());
        volume.setVolumeLoadAssembler(assemblersGroup.getVolumeLoad());

        AssemblerIntegrator neumann = new AssemblerIntegrator();
        neumann.setAssembler(assemblersGroup.getNeumann());

        AssemblerIntegrator dirichlet = new AssemblerIntegrator();
        dirichlet.setAssembler(assemblersGroup.getDirichlet());

        AssemblerIntegratorsGroup result = new AssemblerIntegratorsGroup(volume, neumann, dirichlet);
        return result;
    }

}
