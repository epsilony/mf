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
package net.epsilony.mf.implicit.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.util.NormalGridToPolygonUnitGrid;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.load.ArrayLoadValue;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.util.function.RectangleToGridCoords;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RectangleApproximationModelFactory implements Supplier<AnalysisModel> {

    private ToDoubleFunction<double[]> levelFunction;
    private final Object levelFunctionKey = new Object();
    private MFRectangle rectangle;

    Function<? super MFRectangle, ? extends Collection<double[]>> nodeCoordsGenerator;
    Function<? super MFRectangle, ? extends Collection<? extends PolygonIntegrateUnit>> volumeIntegralUnitsGenerator;

    @Override
    public AnalysisModel get() {
        RawAnalysisModel result = new RawAnalysisModel();
        result.setSpatialDimension(2);
        result.setValueDimension(1);
        result.setSpaceNodes(nodeCoordsGenerator.apply(rectangle).stream().map(MFNode::new)
                .collect(Collectors.toList()));
        result.setIntegrateUnitsGroup(genIntegrateUnitsGroup());
        result.setLoadMap(genLoadMap());
        return result;
    }

    private IntegrateUnitsGroup genIntegrateUnitsGroup() {
        IntegrateUnitsGroup result = new IntegrateUnitsGroup();
        Collection<? extends PolygonIntegrateUnit> volume = volumeIntegralUnitsGenerator.apply(rectangle);
        volume.forEach(u -> u.setLoadKey(levelFunctionKey));
        result.setVolume(new ArrayList<>(volume));
        return result;
    }

    private Map<Object, GeomPointLoad> genLoadMap() {
        Map<Object, GeomPointLoad> result = new HashMap<>();
        result.put(levelFunctionKey, genVolumeNode());
        return result;
    }

    private GeomPointLoad genVolumeNode() {
        return new GeomPointLoad() {
            final ArrayLoadValue resultLoadValue = new ArrayLoadValue();
            final double[] data = new double[1];
            {
                resultLoadValue.setValues(data);
            }

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                data[0] = levelFunction.applyAsDouble(geomPoint.getCoord());
                return resultLoadValue;
            }
        };
    }

    public MFRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

    public void setNodeCoordsGenerator(Function<? super MFRectangle, ? extends Collection<double[]>> nodeCoordsGenerator) {
        this.nodeCoordsGenerator = nodeCoordsGenerator;
    }

    public void setVolumeIntegralUnitsGenerator(
            Function<MFRectangle, Collection<? extends PolygonIntegrateUnit>> volumeIntegralUnitsGenerator) {
        this.volumeIntegralUnitsGenerator = volumeIntegralUnitsGenerator;
    }

    public static class ByNumRowsCols implements Supplier<AnalysisModel> {
        private final RectangleApproximationModelFactory innerFactory = new RectangleApproximationModelFactory();
        private int numNodeCols, numNodeRows, numQuadRows, numQuadCols;

        public ByNumRowsCols(int numNodeRows, int numNodeCols, int numQuadRows, int numQuadCols) {
            this.numNodeRows = numNodeRows;
            this.numNodeCols = numNodeCols;
            this.numQuadRows = numQuadRows;
            this.numQuadCols = numQuadCols;
        }

        public ByNumRowsCols() {
        }

        public int getNumNodeCols() {
            return numNodeCols;
        }

        public void setNumNodeCols(int numNodeCols) {
            this.numNodeCols = numNodeCols;
        }

        public int getNumNodeRows() {
            return numNodeRows;
        }

        public void setNumNodeRows(int numNodeRows) {
            this.numNodeRows = numNodeRows;
        }

        public int getNumQuadRows() {
            return numQuadRows;
        }

        public void setNumQuadRows(int numQuadRows) {
            this.numQuadRows = numQuadRows;
        }

        public int getNumQuadCols() {
            return numQuadCols;
        }

        public void setNumQuadCols(int numQuadCols) {
            this.numQuadCols = numQuadCols;
        }

        @Override
        public AnalysisModel get() {
            innerFactory.setNodeCoordsGenerator(new RectangleToGridCoords.ByNumRowsCols(numNodeRows, numNodeCols)
                    .andThen(crdGrid -> {
                        Stream<double[]> flatMap = crdGrid.stream().flatMap(Collection::stream);
                        return flatMap.collect(Collectors.toList());
                    }));
            innerFactory.setVolumeIntegralUnitsGenerator(new RectangleToGridCoords.ByNumRowsCols(numQuadRows + 1,
                    numQuadCols + 1).andThen(new NormalGridToPolygonUnitGrid()).andThen(polyGrids -> {
                Stream<PolygonIntegrateUnit> flatMap = polyGrids.stream().flatMap(Collection::stream);
                return flatMap.collect(Collectors.toList());
            }));
            return innerFactory.get();
        }

        public MFRectangle getRectangle() {
            return innerFactory.getRectangle();
        }

        public void setRectangle(MFRectangle rectangle) {
            innerFactory.setRectangle(rectangle);
        }

        public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
            innerFactory.setLevelFunction(levelFunction);
        }

    }
}
