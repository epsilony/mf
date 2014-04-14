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
package net.epsilony.mf.integrate.integrator.vc.config;

import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import javax.annotation.Resource;

import net.epsilony.mf.integrate.integrator.config.IntegratorBaseConfig;
import net.epsilony.mf.integrate.integrator.config.IntegratorsGroup;
import net.epsilony.mf.integrate.integrator.config.SimpToAssemblyInputRegistry;
import net.epsilony.mf.integrate.integrator.config.ToAssemblyInputRegistry;
import net.epsilony.mf.integrate.integrator.vc.AsymMixRecordToT2Value;
import net.epsilony.mf.integrate.integrator.vc.CommonVCAssemblyIndexMap;
import net.epsilony.mf.integrate.integrator.vc.IntegralMixRecordEntry;
import net.epsilony.mf.integrate.integrator.vc.MixRecordToAssemblyInput;
import net.epsilony.mf.integrate.integrator.vc.MixRecordToLagrangleAssemblyInput;
import net.epsilony.mf.integrate.integrator.vc.SimpIntegralMixRecorder;
import net.epsilony.mf.integrate.integrator.vc.TransDomainAssemblyIndexToBasesFunction;
import net.epsilony.mf.integrate.integrator.vc.TransDomainPartialVectorFunction;
import net.epsilony.mf.integrate.integrator.vc.VCIntegrator2D;
import net.epsilony.mf.integrate.integrator.vc.VCNode;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.config.MixerConfig;
import net.epsilony.mf.util.bus.BiConsumerRegistry;
import net.epsilony.mf.util.math.PartialVectorFunction;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.mf.util.spring.ContextTools;

import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class VCIntegratorBaseConfig extends ApplicationContextAwareImpl {
    // required
    // must be added to @link{#VC_INTEGRATORS_GROUPS}
    public static final String VC_INTEGRATORS_GROUP_PROTO = "vcIntegratorsGroupProto";
    public static final String VC_TRANS_DOMAIN_BASES_FUNCTION_PROTO = "vcTransDomainBasesFunctionProto";
    public static final String VC_INTEGRAL_NODE_FACTORY = "vcIntegralNodeFactory";

    // end of required

    public static final String TWOD_VC_INTEGRATORS_GROUP_PROTO = "twoDVCIntegratorsGroupProto";
    public static final String VC_INTEGRATORS_GROUPS = "vcIntegratorsGropus";
    @Resource(name = ModelBusConfig.NODES_BUS)
    BiConsumerRegistry<Collection<? extends MFNode>> nodesBus;
    @Resource(name = ModelBusConfig.SPATIAL_DIMENSION_BUS)
    BiConsumerRegistry<Integer> spatialDimensionBus;

    @Bean(name = VC_INTEGRATORS_GROUPS)
    public ArrayList<IntegratorsGroup> vcIntegratorsGroups() {
        return new ArrayList<>();
    }

    @Bean(name = TWOD_VC_INTEGRATORS_GROUP_PROTO)
    @Scope("prototype")
    public IntegratorsGroup twodVCIntegratorsGroupProto() {
        IntegratorsGroup result = new IntegratorsGroup();
        VCIntegrator2D vcIntegrator2D = twodVCIntegratorProto();
        Function<Object, Collection<GeomQuadraturePoint>> commonUnitToPointsProto = getCommonUnitToPointsProto();
        Consumer<GeomQuadraturePoint> volume = vcIntegrator2D::volumeIntegrate;
        result.setVolume(oneStreamConsumer(commonUnitToPointsProto.andThen(Collection::stream), volume));
        Consumer<GeomQuadraturePoint> neumann = vcIntegrator2D::neumannIntegrate;
        result.setNeumann(oneStreamConsumer(commonUnitToPointsProto.andThen(Collection::stream), neumann));
        Consumer<GeomQuadraturePoint> dirichlet = vcIntegrator2D::dirichletIntegrate;
        result.setDirichlet(oneStreamConsumer(commonUnitToPointsProto.andThen(Collection::stream), dirichlet));
        vcIntegratorsGroups().add(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Function<Object, Collection<GeomQuadraturePoint>> getCommonUnitToPointsProto() {
        return (Function<Object, Collection<GeomQuadraturePoint>>) applicationContext
                .getBean(IntegratorBaseConfig.COMMON_UNIT_TO_POINTS_PROTO);
    }

    @Bean
    @Scope("prototype")
    public VCIntegrator2D twodVCIntegratorProto() {
        VCIntegrator2D result = new VCIntegrator2D();
        result.setMixer(getMixerProto());
        result.setNeumannAssociatedRecorder(neumannVCMixRecorder().subRecorder()::add);
        result.setVolumeAssociatedRecorder(volumeVCMixRecorder().subRecorder()::add);
        result.setDirichletAssociatedRecorder(dirichletVCMixRecorder().subRecorder()::add);

        CommonVCAssemblyIndexMap commonAssemblyIndexMap = commonVCAssemblyIndexMap();
        result.setAssemblyIndexToVCNodeGetter(commonAssemblyIndexMap::getVCNode);

        IntFunction<PartialVectorFunction> vcBasesFunctionGetterProto = assemblyIndexToBasesFunctionProto();
        result.setAssemblyIndexToBasesFunction(vcBasesFunctionGetterProto);
        return result;
    }

    public static final String ASSEMBLY_INDEX_TO_VC_BASES_FUNCTION_PROTO = "assemblyIndexToVCBasesFunctionProto";

    @Bean(name = ASSEMBLY_INDEX_TO_VC_BASES_FUNCTION_PROTO)
    @Scope("prototype")
    public IntFunction<PartialVectorFunction> assemblyIndexToBasesFunctionProto() {
        TransDomainPartialVectorFunction function = applicationContext.getBean(VC_TRANS_DOMAIN_BASES_FUNCTION_PROTO,
                TransDomainPartialVectorFunction.class);
        CommonVCAssemblyIndexMap commonAssemblyIndexMap = commonVCAssemblyIndexMap();
        TransDomainAssemblyIndexToBasesFunction result = new TransDomainAssemblyIndexToBasesFunction();
        result.setTranScalePartialVectorFunction(function);
        result.setAssemblyIndexToScale(commonAssemblyIndexMap::getInfluenceRadius);
        result.setAssemblyIndexToOrigin(commonAssemblyIndexMap::getMFNodeCoord);

        return result;
    }

    public static final String COMMON_VC_ASSEMBLY_INDEX_MAP = "commonVCAssemblyIndexMap";

    @Bean(name = COMMON_VC_ASSEMBLY_INDEX_MAP)
    public CommonVCAssemblyIndexMap commonVCAssemblyIndexMap() {
        CommonVCAssemblyIndexMap result = new CommonVCAssemblyIndexMap();
        @SuppressWarnings("unchecked")
        IntFunction<VCNode> vcNodeFactory = applicationContext.getBean(VC_INTEGRAL_NODE_FACTORY, IntFunction.class);
        result.setVcNodeFactoryByAssemblyIndex(vcNodeFactory);
        nodesBus.register(CommonVCAssemblyIndexMap::setNodes, result);
        spatialDimensionBus.register(CommonVCAssemblyIndexMap::setSpatialDimension, result);
        return result;
    }

    public static final String VOLUME_VC_MIX_RECORDER = "volumeVCMixRecorder";

    @Bean(name = VOLUME_VC_MIX_RECORDER)
    public SimpIntegralMixRecorder volumeVCMixRecorder() {
        return new SimpIntegralMixRecorder();
    }

    public static final String DIRICHLET_VC_MIX_RECORDER = "dirichletVCMixRecorder";

    @Bean(name = DIRICHLET_VC_MIX_RECORDER)
    public SimpIntegralMixRecorder dirichletVCMixRecorder() {
        return new SimpIntegralMixRecorder();
    }

    public static final String NEUMANN_VC_MIX_RECORDER = "neumannVCMixRecorder";

    @Bean(name = NEUMANN_VC_MIX_RECORDER)
    public SimpIntegralMixRecorder neumannVCMixRecorder() {
        return new SimpIntegralMixRecorder();
    }

    private MFMixer getMixerProto() {
        return applicationContext.getBean(MixerConfig.MIXER_PROTO, MFMixer.class);
    }

    @Bean
    @Scope("prototype")
    public ToAssemblyInputRegistry vcMixRecordToAssemblyInputRegistryProto() {
        SimpToAssemblyInputRegistry result = new SimpToAssemblyInputRegistry();
        MixRecordToAssemblyInput asymMixRecordToAssemblyInputProto = asymMixRecordToAssemblyInputProto();

        Function<IntegralMixRecordEntry, Stream<AssemblyInput>> function = asymMixRecordToAssemblyInputProto
                .andThen(Stream::of);
        result.volume().put(IntegralMixRecordEntry.class, function);
        result.neumann().put(IntegralMixRecordEntry.class, function);
        @SuppressWarnings("unchecked")
        Function<?, ? extends Stream<AssemblyInput>> lagFunction = (Function<?, ? extends Stream<AssemblyInput>>) asymMixRecordToLagrangleAssemblyInputProto()
                .andThen(Stream::of);
        result.dirichlet().put(IntegralMixRecordEntry.class, lagFunction);
        return result;
    }

    public static final String ASYM_MIX_RECORD_TO_T2_VALUE = "asymMixRecordToT2Value";

    @Bean(name = ASYM_MIX_RECORD_TO_T2_VALUE)
    @Scope("prototype")
    public AsymMixRecordToT2Value asymMixRecordToT2Value() {
        AsymMixRecordToT2Value result = new AsymMixRecordToT2Value();
        result.setAssemblyIndexToIntegralNode(assemblyIndexToIntegralNode());
        result.setAssemblyIndexToVCBasesFunction(assemblyIndexToBasesFunctionProto());
        return result;
    }

    @Bean
    @Scope("prototype")
    public MixRecordToAssemblyInput asymMixRecordToAssemblyInputProto() {
        MixRecordToAssemblyInput result = new MixRecordToAssemblyInput();
        result.setMixRecordToT2Value(asymMixRecordToT2Value());
        result.setLoadValueCalculator(getLoadValueFunction());
        return result;
    }

    private Function<? super GeomPoint, ? extends LoadValue> getLoadValueFunction() {
        @SuppressWarnings("unchecked")
        Function<? super GeomPoint, ? extends LoadValue> loadValueFunction = (Function<? super GeomPoint, ? extends LoadValue>) applicationContext
                .getBean(IntegratorBaseConfig.LOAD_VALUE_FUNCTION_PROTO);
        return loadValueFunction;
    }

    @Bean
    @Scope("prototype")
    public MixRecordToLagrangleAssemblyInput asymMixRecordToLagrangleAssemblyInputProto() {
        MixRecordToLagrangleAssemblyInput result = new MixRecordToLagrangleAssemblyInput();
        result.setMixRecordToT2Value(asymMixRecordToT2Value());
        result.setLoadValueCalculator(getLoadValueFunction());
        return result;
    }

    @Bean
    public IntFunction<VCNode> assemblyIndexToIntegralNode() {
        return commonVCAssemblyIndexMap()::getVCNode;
    }

    public static void addVCBasesDefinition(AnnotationConfigApplicationContext acac,
            Class<? extends TransDomainPartialVectorFunction> type) {
        GenericBeanDefinition definition = ContextTools.definition(type);
        definition.setScope("prototype");
        acac.registerBeanDefinition(VC_TRANS_DOMAIN_BASES_FUNCTION_PROTO, definition);
    }

}
