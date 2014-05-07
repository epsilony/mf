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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.integrate.unit.MFLineUnit;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.load.ArrayDirichletLoadValue;
import net.epsilony.mf.model.load.ArrayLoadValue;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class InitialModelFactory implements Supplier<AnalysisModel> {
    private Collection<? extends PolygonIntegrateUnit> volumeUnits;
    private Collection<? extends MFLine> emphasizeLines;
    // space nodes without Dirichlet Lagrangle load
    private boolean spaceNodesContainingDirichlet = true;
    private Collection<? extends MFNode> spaceNodes;
    private ToDoubleFunction<double[]> levelFunction;
    private final Object volumeLoadKey = new Object();
    private final Object emphasizeLoadKey = new Object();
    private static final int SPATIAL_DIMENSION = 2;
    private Set<MFNode> dirichletSpaceNodes;

    public InitialModelFactory(Collection<? extends PolygonIntegrateUnit> volumeUnits,
            Collection<? extends MFLine> emphasizeLines, Collection<? extends MFNode> spaceNodes,
            boolean spaceNodesContainingDirichlet, ToDoubleFunction<double[]> levelFunction) {
        this.volumeUnits = volumeUnits;
        this.emphasizeLines = emphasizeLines;
        this.spaceNodes = spaceNodes;
        this.spaceNodesContainingDirichlet = spaceNodesContainingDirichlet;
        this.levelFunction = levelFunction;
    }

    public InitialModelFactory() {
    }

    @Override
    public AnalysisModel get() {
        RawAnalysisModel model = new RawAnalysisModel();
        model.setGeomRoot(null);
        model.setIntegrateUnitsGroup(genIntegrateUnitsGroup());
        model.setSpatialDimension(SPATIAL_DIMENSION);
        model.setValueDimension(1);
        model.setSpaceNodes(genAllSpaceNodes());
        model.setLoadMap(genLoadMap());
        putFreeDirichetPredicate(model.getExtraData());
        return model;
    }

    private IntegrateUnitsGroup genIntegrateUnitsGroup() {
        IntegrateUnitsGroup integrateUnitsGroup = new IntegrateUnitsGroup();

        if (null != emphasizeLines && !emphasizeLines.isEmpty()) {
            List<MFLineUnit> dirichletUnit = emphasizeLines.stream().map(this::toLineUnit).collect(Collectors.toList());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final List<Object> dirichlet = (List) dirichletUnit;
            integrateUnitsGroup.setDirichlet(dirichlet);
        }
        volumeUnits.forEach(unit -> unit.setLoadKey(volumeLoadKey));
        List<Object> volume = new ArrayList<>(volumeUnits);
        integrateUnitsGroup.setVolume(volume);

        return integrateUnitsGroup;
    }

    private MFLineUnit toLineUnit(MFLine line) {
        return new MFLineUnit(false, line, emphasizeLoadKey);
    }

    private List<MFNode> genAllSpaceNodes() {
        List<MFNode> allSpaceNodes = spaceNodes.stream().collect(Collectors.toList());
        if (emphasizeLines != null && !emphasizeLines.isEmpty()) {
            dirichletSpaceNodes = new LinkedHashSet<MFNode>();
            for (MFLine line : emphasizeLines) {
                dirichletSpaceNodes.add((MFNode) line.getStart());
                dirichletSpaceNodes.add((MFNode) line.getEnd());
            }
            if (!spaceNodesContainingDirichlet) {
                allSpaceNodes.addAll(dirichletSpaceNodes);
            }
        }
        return allSpaceNodes;
    }

    private Map<Object, GeomPointLoad> genLoadMap() {
        Map<Object, GeomPointLoad> result = new HashMap<>();
        result.put(volumeLoadKey, volumeLoad());
        result.put(emphasizeLoadKey, emphasiszeLoad());
        return result;
    }

    private GeomPointLoad volumeLoad() {
        return new GeomPointLoad() {

            private final double[] data = new double[1];
            private final ArrayLoadValue result = new ArrayLoadValue(data);

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                data[0] = levelFunction.applyAsDouble(geomPoint.getCoord());
                return result;
            }
        };
    }

    private GeomPointLoad emphasiszeLoad() {
        return new GeomPointLoad() {
            private final double[] data = new double[1];
            private final ArrayDirichletLoadValue result = new ArrayDirichletLoadValue(data, new boolean[] { true });

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                data[0] = levelFunction.applyAsDouble(geomPoint.getCoord());
                return result;
            }
        };
    }

    private void putFreeDirichetPredicate(Map<String, Object> extraData) {
        if (null != dirichletSpaceNodes) {
            Predicate<MFNode> spaceDirichletNodePredicate = dirichletSpaceNodes::contains;
            extraData.put(AnalysisModel.SPACE_DIRICHLET_NODE_PRIDICATE, spaceDirichletNodePredicate);
        } else {
            extraData.put(AnalysisModel.SPACE_DIRICHLET_NODE_PRIDICATE, null);
        }

    }

    public Collection<? extends PolygonIntegrateUnit> getVolumeUnits() {
        return volumeUnits;
    }

    public void setVolumeUnits(Collection<? extends PolygonIntegrateUnit> volumeUnits) {
        this.volumeUnits = volumeUnits;
    }

    public Collection<? extends MFLine> getEmphasizeLines() {
        return emphasizeLines;
    }

    public void setEmphasizeLines(Collection<? extends MFLine> emphasizeLines) {
        for (MFLine line : emphasizeLines) {
            if (line.getSucc() == null) {
                throw new IllegalArgumentException();
            }
        }
        this.emphasizeLines = emphasizeLines;
    }

    public Collection<? extends MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(Collection<? extends MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public ToDoubleFunction<double[]> getLevelFunction() {
        return levelFunction;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

    public boolean isSpaceNodesContainingDirichlet() {
        return spaceNodesContainingDirichlet;
    }

    public void setSpaceNodesContainingDirichlet(boolean spaceNodesContainingDirichlet) {
        this.spaceNodesContainingDirichlet = spaceNodesContainingDirichlet;
    }

}
