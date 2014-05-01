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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.config.CommonAnalysisModelHubConfig;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFFacetFactory;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;
import net.epsilony.mf.model.geom.util.MFLineChainFactory;
import net.epsilony.mf.model.load.DirichletLoadValue;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.RecorderAssembler;
import net.epsilony.mf.process.assembler.config.AssemblerBaseConfig;
import net.epsilony.mf.process.assembler.config.AssemblersGroup;
import net.epsilony.mf.process.mix.MFMixer;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.MFUtils;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ContextTools;
import net.epsilony.tb.analysis.Math2D;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class IntegratorLagrangleConfigTest {

    private RawAnalysisModel model2d, model1d;
    private final Function<double[], Double> linearFunc = (xy) -> 3 * xy[0] + 4 * xy[1];
    private double volIntegral2d;
    private double neuIntegral2d;
    private double diriIntegral2d;
    private double neuIntegral1d;
    private double diriIntegral1d;
    private double volIntegral1d;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testTwoD() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(IntegratorLagrangleConfig.class,
                CommonAnalysisModelHubConfig.class, AssemblerBaseConfig.class, MockAssemblerConfig.class,
                MockMixerConfig.class);

        ContextTools.notReallyProtoBeans(ac).forEach((name) -> System.out.println(name));
        CommonAnalysisModelHub hub = ac.getBean(CommonAnalysisModelHub.class);
        hub.setAnalysisModel(model2d);
        IntegratorsGroup intGroup = ac.getBean(IntegratorBaseConfig.INTEGRATORS_GROUP_PROTO, IntegratorsGroup.class);
        IntegrateUnitsGroup intUnitsGroup = model2d.getIntegrateUnitsGroup();
        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) ac
                .getBean(CommonToPointsIntegratorConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.postToFresh(2);
        intUnitsGroup.getVolume().stream().forEach((Consumer) intGroup.getVolume());
        List<AssemblersGroup> assemblersGroupList = (List<AssemblersGroup>) ac
                .getBean(AssemblerBaseConfig.ASSEMBLERS_GROUPS);

        assertEquals(1, assemblersGroupList.size());
        AssemblersGroup asmGrp = assemblersGroupList.get(0);
        assertAssemblerRecords((RecorderAssembler) asmGrp.getVolume(), volIntegral2d);
        intUnitsGroup.getDirichlet().stream().forEach((Consumer) intGroup.getDirichlet());
        assertAssemblerRecords((RecorderAssembler) asmGrp.getDirichlet(), diriIntegral2d);
        intUnitsGroup.getNeumann().stream().forEach((Consumer) intGroup.getNeumann());
        assertAssemblerRecords((RecorderAssembler) asmGrp.getNeumann(), neuIntegral2d);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test1D() {
        @SuppressWarnings("resource")
        ApplicationContext ac = new AnnotationConfigApplicationContext(IntegratorLagrangleConfig.class,
                CommonAnalysisModelHubConfig.class, AssemblerBaseConfig.class, MockAssemblerConfig.class,
                MockMixerConfig.class);
        CommonAnalysisModelHub hub = ac.getBean(CommonAnalysisModelHub.class);

        hub.setAnalysisModel(model1d);
        IntegratorsGroup intGroup = ac.getBean(IntegratorBaseConfig.INTEGRATORS_GROUP_PROTO, IntegratorsGroup.class);
        IntegrateUnitsGroup intUnitsGroup = model1d.getIntegrateUnitsGroup();

        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) ac
                .getBean(CommonToPointsIntegratorConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.postToFresh(2);
        intUnitsGroup.getVolume().stream().forEach((Consumer) intGroup.getVolume());
        List<AssemblersGroup> assemblersGroupList = (List<AssemblersGroup>) ac
                .getBean(AssemblerBaseConfig.ASSEMBLERS_GROUPS);

        assertEquals(1, assemblersGroupList.size());
        AssemblersGroup asmGrp = assemblersGroupList.get(0);
        assertAssemblerRecords((RecorderAssembler) asmGrp.getVolume(), volIntegral1d);
        intUnitsGroup.getDirichlet().stream().forEach((Consumer) intGroup.getDirichlet());
        assertAssemblerRecords((RecorderAssembler) asmGrp.getDirichlet(), diriIntegral1d);
        intUnitsGroup.getNeumann().stream().forEach((Consumer) intGroup.getNeumann());
        assertAssemblerRecords((RecorderAssembler) asmGrp.getNeumann(), neuIntegral1d);

    }

    private void assertAssemblerRecords(RecorderAssembler asm, double expInt) {
        List<AssemblyInput> inputRecords = asm.getInputRecords();
        double res = 0;
        for (AssemblyInput ai : inputRecords) {
            LoadValue loadValue = ai.getLoadValue();
            res += loadValue.value(0) * ai.getWeight();
        }
        assertEquals(expInt, res, 1e-12);
    }

    @Before
    public void genModelTwoD() {
        model2d = new RawAnalysisModel();
        model2d.setValueDimension(1);
        model2d.setSpatialDimension(2);
        final MFFacetFactory mfFacetFactory = new MFFacetFactory(SimpMFLine::new, MFNode::new);
        MFFacet facet = mfFacetFactory.produceBySingleChain(Arrays.asList(new double[][] { { 0, 0 }, { 1, 0 },
                { 1, 1 }, { 0, 1 } }));
        model2d.setGeomRoot(facet);
        model2d.setSpaceNodes(Arrays.asList(new MFNode(0.5, 0.5)));
        ArrayList<MFLine> segs = Lists.newArrayList(facet);
        HashMap<Object, GeomPointLoad> loadMap = new HashMap<>();

        MFLine diriSeg = segs.get(0);
        loadMap.put(diriSeg, dirichletLoad(linearFunc));
        diriIntegral2d = linearFunc.apply(MFLine2DUtils.chordMidPoint(diriSeg, null))
                * MFLine2DUtils.chordLength(diriSeg);
        MFLine neuSeg = segs.get(2);
        loadMap.put(neuSeg, neumannLoad(linearFunc));

        neuIntegral2d = linearFunc.apply(MFLine2DUtils.chordMidPoint(neuSeg, null)) * MFLine2DUtils.chordLength(neuSeg);

        loadMap.put(facet, neumannLoad(linearFunc));
        model2d.setLoadMap(loadMap);

        IntegrateUnitsGroup intUnitsGrp = new IntegrateUnitsGroup();
        intUnitsGrp.setDirichlet(Arrays.asList(diriSeg));
        intUnitsGrp.setNeumann(Arrays.asList(neuSeg));
        PolygonIntegrateUnit tri = new PolygonIntegrateUnit();
        tri.setVertesCoords(new double[][] { { 0.2, 0.2 }, { 0.7, 0.2 }, { 0.4, 0.4 } });
        tri.setLoadKey(facet);
        double triInt = triLinearFuncIntegrate(tri.getVertesCoords(), linearFunc);
        PolygonIntegrateUnit quad = new PolygonIntegrateUnit();
        double[][] qv = new double[][] { { 0.7, 0.1 }, { 0.8, 0.2 }, { 0.81, 0.3 }, { 0.72, 0.3 } };
        quad.setVertesCoords(qv);
        quad.setLoadKey(facet);
        intUnitsGrp.setVolume(Arrays.asList(tri, quad));
        double quadInt = triLinearFuncIntegrate(new double[][] { qv[0], qv[1], qv[2] }, linearFunc);
        quadInt += triLinearFuncIntegrate(new double[][] { qv[0], qv[2], qv[3] }, linearFunc);
        volIntegral2d = triInt + quadInt;
        model2d.setIntegrateUnitsGroup(intUnitsGrp);
    }

    @Before
    public void genModelOneD() {
        model1d = new RawAnalysisModel();
        double[] xs = MFUtils.linSpace(0.5, 8.5, 3);
        double[] ys = MFUtils.linSpace(-1, 4, 3);
        ArrayList<double[]> coords = new ArrayList<>();
        for (int i = 0; i < xs.length; i++) {
            double[] coord = { xs[i], ys[i] };
            coords.add(coord);
        }
        MFLine chain = new MFLineChainFactory(SimpMFLine::new, MFNode::new, false).produce(coords);
        List<MFNode> nodes = StreamSupport.stream(chain.spliterator(), false).map(line -> (MFNode) line.getStart())
                .collect(Collectors.toList());
        model1d.setGeomRoot(chain);
        model1d.setSpaceNodes(Arrays.asList(nodes.get(2)));
        model1d.setSpatialDimension(1);
        model1d.setValueDimension(2);

        MFNode diriNode = nodes.get(0);

        MFNode neuNode = nodes.get(nodes.size() - 1);

        IntegrateUnitsGroup integrateUnitsGroup = new IntegrateUnitsGroup();
        ArrayList<Object> volumeUnits = Lists.newArrayList(chain);
        volumeUnits.remove(volumeUnits.size() - 1);
        integrateUnitsGroup.setVolume(volumeUnits);
        volIntegral1d = MathArrays.distance(diriNode.getCoord(), neuNode.getCoord());
        volIntegral1d *= linearFunc.apply(MathArrays.scale(0.5,
                MathArrays.ebeAdd(diriNode.getCoord(), neuNode.getCoord())));
        integrateUnitsGroup.setDirichlet(Arrays.asList(diriNode));
        diriIntegral1d = linearFunc.apply(diriNode.getCoord());
        integrateUnitsGroup.setNeumann(Arrays.asList(neuNode));
        neuIntegral1d = linearFunc.apply(neuNode.getCoord());
        model1d.setIntegrateUnitsGroup(integrateUnitsGroup);

        Map<Object, GeomPointLoad> loadMap = new HashMap<>();
        for (Object u : volumeUnits) {
            loadMap.put(u, neumannLoad(linearFunc));
        }
        loadMap.put(diriNode, dirichletLoad(linearFunc));
        loadMap.put(neuNode, neumannLoad(linearFunc));
        model1d.setLoadMap(loadMap);
    }

    private double triLinearFuncIntegrate(double[][] tri, Function<double[], Double> func) {
        double area = Math2D.area(tri);
        double[] center = MathArrays.ebeAdd(tri[0], tri[1]);
        center = MathArrays.ebeAdd(center, tri[2]);
        center = MathArrays.scale(1 / 3.0, center);
        return area * func.apply(center);
    }

    private GeomPointLoad dirichletLoad(Function<double[], Double> func) {
        return new GeomPointLoad() {

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                return new MockLoadValue(new double[] { func.apply(geomPoint.getCoord()) },
                        new boolean[] { true, false });
            }

            @Override
            public boolean isDirichlet() {
                return true;
            }
        };

    }

    private GeomPointLoad neumannLoad(Function<double[], Double> func) {
        return new GeomPointLoad() {

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                return new MockLoadValue(new double[] { func.apply(geomPoint.getCoord()) },
                        new boolean[] { true, false });
            }

        };
    }

    static class MockLoadValue implements DirichletLoadValue {

        double[] data;
        boolean[] validity;

        @Override
        public double value(int dimIndex) {
            return data[dimIndex];
        }

        @Override
        public int size() {
            return data.length;
        }

        @Override
        public boolean validity(int dimIndex) {
            return validity[dimIndex];
        }

        /**
         * @param data
         * @param validity
         */
        public MockLoadValue(double[] data, boolean[] validity) {
            super();
            this.data = data;
            this.validity = validity;
        }

    }

    @Configuration
    public static class MockMixerConfig {

        // @Resource(name = ModelBusConfig.SPATIAL_DIMENSION_BUS)
        // BiConsumerRegistry<Integer> spatialDimensionBus;

        @Bean(name = MixerConfig.MIXER_PROTO)
        @Scope("prototype")
        MFMixer mixerProto() {
            return new MockMixer();
        }
    }

    static class MockMixer implements MFMixer {
        MFGeomUnit boundary;
        double[] center;
        int diffOrder;

        public MFGeomUnit getBoundary() {
            return boundary;
        }

        @Override
        public void setBoundary(MFGeomUnit boundary) {
            this.boundary = boundary;
        }

        public double[] getCenter() {
            return center;
        }

        @Override
        public void setCenter(double[] center) {
            this.center = center;
        }

        @Override
        public int getDiffOrder() {
            return diffOrder;
        }

        @Override
        public void setDiffOrder(int diffOrder) {
            this.diffOrder = diffOrder;
        }

        @Override
        public void setUnitOutNormal(double[] unitNormal) {

        }

        @Override
        public ShapeFunctionValue mix() {
            return new MockShapeFunctionValue(center, boundary);
        }
    }

    static class MockShapeFunctionValue extends MockMixer implements ShapeFunctionValue {

        public MockShapeFunctionValue(double[] center, MFGeomUnit boundary) {
            this.center = center;
            this.boundary = boundary;
        }

        @Override
        public int getNodeAssemblyIndex(int nd) {

            return 0;
        }

        @Override
        public int getSpatialDimension() {

            return 0;
        }

        @Override
        public int size() {

            return 0;
        }

        @Override
        public int getMaxPartialOrder() {

            return 0;
        }

        @Override
        public double get(int index, int partialIndex) {

            return 0;
        }

        @Override
        public ShapeFunctionValue copy() {

            return null;
        }

    }

    @Configuration
    public static class MockAssemblerConfig {
        @Bean(name = AssemblerBaseConfig.VOLUME_ASSEMBLER_PROTO)
        @Scope("prototype")
        RecorderAssembler volumeAssemblerProto() {
            return new RecorderAssembler();
        }

        @Bean(name = AssemblerBaseConfig.DIRICHLET_ASSEMBLER_PROTO)
        @Scope("prototype")
        RecorderAssembler dirichletAssemblerProto() {
            return new RecorderAssembler();
        }

        @Bean(name = AssemblerBaseConfig.NEUMANN_ASSEMBLER_PROTO)
        @Scope("prototype")
        RecorderAssembler neumannAssemblerProto() {
            return new RecorderAssembler();
        }
    }

}
