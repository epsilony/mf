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
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.util.NormalGridToPolygonUnitGrid;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.util.function.RectangleToGridCoords;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class RectangleRangeInitalModelFactory implements Supplier<AnalysisModel> {
    protected final InitialModelFactory initialModelFactory = new InitialModelFactory();
    protected MFRectangle range;

    public Collection<? extends MFLine> getEmphasizeChainHeads() {
        return initialModelFactory.getEmphasizeLines();
    }

    public void setEmphasizeLines(Collection<? extends MFLine> emphasizeLines) {
        initialModelFactory.setEmphasizeLines(emphasizeLines);
    }

    public Collection<? extends MFNode> getSpaceNodes() {
        return initialModelFactory.getSpaceNodes();
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        initialModelFactory.setLevelFunction(levelFunction);
    }

    public MFRectangle getRange() {
        return range;
    }

    public void setRange(MFRectangle range) {
        this.range = range;
    }

    public static class ByNumRowsCols extends RectangleRangeInitalModelFactory {

        private int numNodeCols, numNodeRows, numQuadRows, numQuadCols;

        public ByNumRowsCols(MFRectangle range, int numNodeRows, int numNodeCols, int numQuadRows, int numQuadCols) {
            this.range = range;
            this.numNodeRows = numNodeRows;
            this.numNodeCols = numNodeCols;
            this.numQuadRows = numQuadRows;
            this.numQuadCols = numQuadCols;
        }

        public ByNumRowsCols() {
        }

        @Override
        public AnalysisModel get() {
            initialModelFactory.setSpaceNodes(genSpaceNodes());
            initialModelFactory.setSpaceNodesContainingDirichlet(false);
            initialModelFactory.setVolumeUnits(genVolumeUnits());
            return initialModelFactory.get();
        }

        private List<MFNode> genSpaceNodes() {
            RectangleToGridCoords rectToGridCoords = new RectangleToGridCoords.ByNumRowsCols(numNodeRows, numNodeCols);
            return rectToGridCoords.apply(range).stream().flatMap(Collection::stream).map(MFNode::new)
                    .collect(Collectors.toList());
        }

        private List<PolygonIntegrateUnit> genVolumeUnits() {
            Function<MFRectangle, ArrayList<ArrayList<PolygonIntegrateUnit>>> rectToUnitGrid = new RectangleToGridCoords.ByNumRowsCols(
                    numQuadRows + 1, numQuadCols + 1).andThen(new NormalGridToPolygonUnitGrid());
            return rectToUnitGrid.apply(range).stream().flatMap(Collection::stream).collect(Collectors.toList());

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

    }

}
