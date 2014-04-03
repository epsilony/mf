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
package net.epsilony.mf.model.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.util.NormalGridToPolygonUnitGrid;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.function.ChainFractionizer;
import net.epsilony.mf.model.function.FacetFractionizer;
import net.epsilony.mf.model.function.FacetFractionizer.FacetFractionResult;
import net.epsilony.mf.model.function.SingleLineFractionizer;
import net.epsilony.mf.model.load.ArrayDirichletLoadValue;
import net.epsilony.mf.model.load.ArrayLoadValue;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.util.function.DoubleValueFunction;
import net.epsilony.mf.util.function.GridInnerPicker;
import net.epsilony.mf.util.function.RectangleToGridCoords;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

import org.apache.commons.math3.util.MathArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PoissonPatchModelFactory2D implements Supplier<AnalysisModel> {
    private MFRectangle rectangle;
    private DoubleValueFunction<double[]> field;
    private Function<double[], double[]> fieldGradient;
    private DoubleValueFunction<double[]> source;
    private Function<Facet, Facet> facetFractionizer;
    private Function<MFRectangle, List<double[]>> spaceNodesCoordsGenerator;
    private Function<MFRectangle, List<? extends PolygonIntegrateUnit>> volumeUnitsGenerator;
    public static final Logger logger = LoggerFactory.getLogger(PoissonPatchModelFactory2D.class);

    @Override
    public AnalysisModel get() {
        RawAnalysisModel result = new RawAnalysisModel();
        Facet facet = genFacet();
        result.setGeomRoot(facet);
        result.setSpaceNodes(genSpaceNodes());
        result.setSpatialDimension(2);
        result.setValueDimension(1);
        result.setLoadMap(genLoadMap(facet));
        result.setIntegrateUnitsGroup(genIntegrateUnitsGroup(facet));
        return result;
    }

    @Configuration
    public static class SampleConfig {

        private final int defaultGridRowColNum = 4;

        @Bean
        public PoissonPatchModelFactory2D poissonPatchModelFactory2D() {
            PoissonPatchModelFactory2D result = new PoissonPatchModelFactory2D();

            result.setSource(source());
            result.setFieldGradient(fieldGradient());
            result.setField(field());

            result.setRectangle(rectangle());
            result.setFacetFractionizer(facetFractionizer());
            result.setSpaceNodesCoordsGenerator(spaceNodesCoordsGenerator());
            result.setVolumeUnitsGenerator(volumeUnitsGenerator());
            return result;
        }

        private final double a = 3, b = -2, c = 4, d = 0.5, e = -1, f = -3;

        @Bean
        public DoubleValueFunction<double[]> source() {
            return (xy) -> {
                return 2 * a + 2 * c;
            };
        }

        @Bean
        public Function<double[], double[]> fieldGradient() {
            return (xy) -> {
                double x = xy[0];
                double y = xy[1];
                return new double[] { 2 * a * x + b * y + d, b * x + 2 * c * y + e };
            };
        }

        @Bean
        public DoubleValueFunction<double[]> field() {
            return (xy) -> {
                double x = xy[0];
                double y = xy[1];
                return a * x * x + b * x * y + c * y * y + d * x + e * y + f;
            };
        }

        @Bean
        public Function<MFRectangle, List<? extends PolygonIntegrateUnit>> volumeUnitsGenerator() {
            return rectangleToGrids().andThen(new NormalGridToPolygonUnitGrid()).andThen(Iterables::concat)
                    .andThen(Lists::newArrayList);
        }

        @Bean
        public Function<MFRectangle, List<double[]>> spaceNodesCoordsGenerator() {
            RectangleToGridCoords.ByNumRowsCols rectToGrids = rectangleToGrids();

            return rectToGrids.andThen(new GridInnerPicker<>()).andThen(Iterables::concat).andThen(Lists::newArrayList);

        }

        @Bean
        public RectangleToGridCoords.ByNumRowsCols rectangleToGrids() {
            RectangleToGridCoords.ByNumRowsCols rectToGrids = new RectangleToGridCoords.ByNumRowsCols();
            rectToGrids.setNumCols(defaultGridRowColNum);
            rectToGrids.setNumRows(defaultGridRowColNum);
            return rectToGrids;
        }

        @Bean
        public Function<Facet, Facet> facetFractionizer() {

            ChainFractionizer chainFractionizer = new ChainFractionizer();
            chainFractionizer.setNodeFactory(MFNode::new);
            chainFractionizer.setSingleLineFractionier(singleLineFractionier());
            FacetFractionizer facetFractionizer = new FacetFractionizer();
            facetFractionizer.setChainFractionier(chainFractionizer);
            return facetFractionizer.andThen(FacetFractionResult::getFacet);
        }

        @Bean
        public SingleLineFractionizer.ByNumberOfNewCoords singleLineFractionier() {
            SingleLineFractionizer.ByNumberOfNewCoords result = new SingleLineFractionizer.ByNumberOfNewCoords(
                    defaultGridRowColNum - 1);
            return result;
        }

        @Bean
        public MFRectangle rectangle() {
            MFRectangle result = new MFRectangle();
            result.setDrul(new double[] { -1, 1, 1, -1 });
            return result;
        }

    }

    public void setRectangle(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public void setField(DoubleValueFunction<double[]> field) {
        this.field = field;
    }

    public void setFieldGradient(Function<double[], double[]> fieldGradient) {
        this.fieldGradient = fieldGradient;
    }

    public void setSource(DoubleValueFunction<double[]> source) {
        this.source = source;
    }

    public void setFacetFractionizer(Function<Facet, Facet> facetFractionizer) {
        this.facetFractionizer = facetFractionizer;
    }

    public void setSpaceNodesCoordsGenerator(Function<MFRectangle, List<double[]>> spaceNodesCoordsGenerator) {
        this.spaceNodesCoordsGenerator = spaceNodesCoordsGenerator;
    }

    public void setVolumeUnitsGenerator(Function<MFRectangle, List<? extends PolygonIntegrateUnit>> volumeUnitsGenerator) {
        this.volumeUnitsGenerator = volumeUnitsGenerator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private IntegrateUnitsGroup genIntegrateUnitsGroup(Facet facet) {
        List<? extends PolygonIntegrateUnit> volumes = volumeUnitsGenerator.apply(rectangle);
        IntegrateUnitsGroup result = new IntegrateUnitsGroup();
        result.setVolume((List) volumes);

        ArrayList<Object> neumanns = new ArrayList<>();
        ArrayList<Object> dirichlets = new ArrayList<>();
        for (Segment seg : facet) {
            if (isDirichlet(seg)) {
                dirichlets.add(seg);
            } else {
                neumanns.add(seg);
            }
        }
        result.setDirichlet(dirichlets);
        result.setNeumann(neumanns);
        return result;
    }

    private Facet genFacet() {
        Facet ori = rectangle.toFacet(MFNode::new);
        return facetFractionizer.apply(ori);
    }

    private List<MFNode> genSpaceNodes() {
        List<double[]> coords = spaceNodesCoordsGenerator.apply(rectangle);
        ArrayList<MFNode> result = new ArrayList<>(coords.size());
        coords.stream().forEach((crd) -> {
            result.add(new MFNode(crd));
        });
        return result;
    }

    private Map<GeomUnit, GeomPointLoad> genLoadMap(Facet facet) {
        Map<GeomUnit, GeomPointLoad> loadMap = new HashMap<>();
        loadMap.put(facet, genVolumeLoad());
        GeomPointLoad diriLoad = genDirichletLoad();
        GeomPointLoad neuLoad = genNeumannLoad();
        for (Segment seg : facet) {
            if (isDirichlet(seg)) {
                loadMap.put(seg, diriLoad);
            } else {
                loadMap.put(seg, neuLoad);
            }
        }
        return loadMap;
    }

    private boolean isDirichlet(Segment seg) {
        return seg.getStart().getCoord()[1] == rectangle.getUp() && seg.getEnd().getCoord()[1] == rectangle.getUp();
    }

    private GeomPointLoad genNeumannLoad() {
        return new GeomPointLoad() {

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                Segment seg = (Segment) geomPoint.getGeomUnit();
                double[] outNormal = Segment2DUtils.chordUnitOutNormal(seg, null);
                double[] grad = fieldGradient.apply(geomPoint.getCoord());
                double neu = MathArrays.linearCombination(outNormal, grad);
                ArrayLoadValue result = new ArrayLoadValue();
                result.setValues(new double[] { neu });
                return result;
            }
        };
    }

    private GeomPointLoad genVolumeLoad() {
        return new GeomPointLoad() {

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                ArrayLoadValue result = new ArrayLoadValue();
                result.setValues(new double[] { source.value(geomPoint.getCoord()) });
                return result;
            }
        };
    }

    private GeomPointLoad genDirichletLoad() {
        return new GeomPointLoad() {
            final boolean[] validities = new boolean[] { true };

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                ArrayDirichletLoadValue result = new ArrayDirichletLoadValue();
                result.setValidities(validities);
                result.setValues(new double[] { field.value(geomPoint.getCoord()) });
                return result;
            }

            @Override
            public boolean isDirichlet() {
                return true;
            };
        };
    }
}
