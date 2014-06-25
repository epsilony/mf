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

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.integrator.config.MFConsumerGroup;
import net.epsilony.mf.integrate.integrator.config.MFFunctionGroup;
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
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.mix.MFMixer;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.util.math.PartialVectorFunction;
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.RelayParmContainerBuilder;
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
    public static final String VC_INTEGRATORS_GROUP_PROTO           = "vcIntegratorsGroupProto";
    public static final String VC_TRANS_DOMAIN_BASES_FUNCTION_PROTO = "vcTransDomainBasesFunctionProto";
    public static final String VC_INTEGRAL_NODE_FACTORY             = "vcIntegralNodeFactory";

    @Bean
    public MFParmContainer vcIntegratorBaseParmContainer() {
        return new RelayParmContainerBuilder().addParms("nodes", "spatialDimension").get();
    }

    public static final String VC_INTEGRATORS_GROUPS = "vcIntegratorsGropus";

    @Bean(name = VC_INTEGRATORS_GROUPS)
    public ArrayList<MFConsumerGroup<GeomQuadraturePoint>> vcIntegratorsGroups() {
        return new ArrayList<>();
    }

    public static final String VC_MIX_RECORD_TO_ASSEMBLY_INPUT_GROUPS = "vcMixRecordToAssemblyInputGroups";

    @Bean(name = VC_MIX_RECORD_TO_ASSEMBLY_INPUT_GROUPS)
    public ArrayList<MFFunctionGroup<IntegralMixRecordEntry, Stream<AssemblyInput>>> vcMixRecordToAssemblyInputGroup() {
        return new ArrayList<>();
    }

    public static final String VC_MIX_RECORD_TO_ASSEMBLY_INPUT_GROUP_PROTO = "vcMixRecordToAssemblyInputGroupProto";

    @Bean(name = VC_MIX_RECORD_TO_ASSEMBLY_INPUT_GROUP_PROTO)
    @Scope("prototype")
    public MFFunctionGroup<IntegralMixRecordEntry, Stream<AssemblyInput>> vcMixRecordToAssemblyInputGroupProto() {
        MixRecordToAssemblyInput asymMixRecordToAssemblyInputProto = asymMixRecordToAssemblyInputProto();
        Function<IntegralMixRecordEntry, Stream<AssemblyInput>> function = asymMixRecordToAssemblyInputProto
                .andThen(Stream::of);

        Function<IntegralMixRecordEntry, Stream<AssemblyInput>> diriFunction;
        if (applicationContext.containsBean(IntegralBaseConfig.IS_LAGRANGLE_DIRICHLET)
                && !applicationContext.getBean(IntegralBaseConfig.IS_LAGRANGLE_DIRICHLET, Boolean.class)) {
            diriFunction = function;
        } else {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Function<IntegralMixRecordEntry, Stream<AssemblyInput>> t = (Function) asymMixRecordToLagrangleAssemblyInputProto()
                    .andThen(Stream::of);
            diriFunction = t;
        }
        MFFunctionGroup<IntegralMixRecordEntry, Stream<AssemblyInput>> result = new MFFunctionGroup<IntegralMixRecordEntry, Stream<AssemblyInput>>(
                function, function, diriFunction);
        return result;
    }

    public static final String TWOD_VC_INTEGRATORS_GROUP_PROTO = "twoDVCIntegratorsGroupProto";

    @Bean(name = TWOD_VC_INTEGRATORS_GROUP_PROTO)
    @Scope("prototype")
    public MFConsumerGroup<GeomQuadraturePoint> twodVCIntegratorsGroupProto() {

        VCIntegrator2D vcIntegrator2D = twodVCIntegratorProto();
        Consumer<GeomQuadraturePoint> volume = vcIntegrator2D::volumeIntegrate;
        Consumer<GeomQuadraturePoint> neumann = vcIntegrator2D::neumannIntegrate;
        Consumer<GeomQuadraturePoint> dirichlet = vcIntegrator2D::dirichletIntegrate;
        MFConsumerGroup<GeomQuadraturePoint> result = new MFConsumerGroup<>(volume, neumann, dirichlet);
        vcIntegratorsGroups().add(result);
        return result;
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
        vcIntegratorBaseParmContainer().autoRegister(result);
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

    public static final String ASYM_MIX_RECORD_TO_T2_VALUE = "asymMixRecordToT2Value";

    @Bean(name = ASYM_MIX_RECORD_TO_T2_VALUE)
    @Scope("prototype")
    public AsymMixRecordToT2Value asymMixRecordToT2Value() {
        AsymMixRecordToT2Value result = new AsymMixRecordToT2Value();
        result.setAssemblyIndexToIntegralNode(assemblyIndexToIntegralNode());
        result.setAssemblyIndexToVCBasesFunction(assemblyIndexToBasesFunctionProto());
        return result;
    }

    public static final String ASYM_MIX_RECORD_TO_ASSEMBLY_INPUT_PROTO = "asymMixRecordToAssemblyInputProto";

    @Bean(name = ASYM_MIX_RECORD_TO_ASSEMBLY_INPUT_PROTO)
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
                .getBean(IntegralBaseConfig.LOAD_VALUE_FUNCTION_PROTO);
        return loadValueFunction;
    }

    public static final String ASYM_MIX_RECORD_TO_LAGRANGLE_ASSEMBLY_INPUT_PROTO = "asymMixRecordToLagrangleAssemblyInputProto";

    @Bean(name = ASYM_MIX_RECORD_TO_LAGRANGLE_ASSEMBLY_INPUT_PROTO)
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
