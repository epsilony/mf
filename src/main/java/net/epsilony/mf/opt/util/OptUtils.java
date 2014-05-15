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
package net.epsilony.mf.opt.util;

import java.util.stream.Collectors;

import net.epsilony.mf.implicit.sample.InitialModelFactory;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.opt.LevelOptModel;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptUtils {

    public static AnalysisModel toInitalAnalysisModel(LevelOptModel levelOptModel) {
        InitialModelFactory factory = new InitialModelFactory();
        factory.setEmphasizeLines(levelOptModel.getRangeBarrier().getFixed());
        factory.setSpaceNodes(levelOptModel.getLevelFunctionNodes());
        factory.setSpaceNodesContainingDirichlet(true);
        factory.setLevelFunction(levelOptModel.getStartLevelFunction());
        factory.setVolumeUnits(levelOptModel.getCells().stream().map(OptUtils::cellToUnit).collect(Collectors.toList()));
        return factory.get();
    }

    public static PolygonIntegrateUnit cellToUnit(MFCell cell) {
        PolygonIntegrateUnit unit = new PolygonIntegrateUnit();
        double[][] coords = new double[cell.vertesSize()][];
        for (int i = 0; i < cell.vertesSize(); i++) {
            coords[i] = cell.getVertexCoord(i);
        }
        unit.setVertesCoords(coords);
        return unit;
    }
}
