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
package net.epsilony.mf.implicit.contour;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class InTriangleEdgeToPolygon implements Function<MFEdge, PolygonIntegrateUnit> {
    private ToDoubleFunction<double[]> levelFunction;

    public InTriangleEdgeToPolygon() {
    }

    public InTriangleEdgeToPolygon(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

    @Override
    public PolygonIntegrateUnit apply(MFEdge edge) {
        MFCell cell = edge.getCell();
        if (cell.vertesSize() != 3) {
            throw new IllegalArgumentException();
        }
        double[] vertesLevels = new double[3];

        int negativeVertesNum = 0;
        for (int i = 0; i < vertesLevels.length; i++) {
            double lv = levelFunction.applyAsDouble(cell.getVertexCoord(i));
            if (lv < 0) {
                negativeVertesNum++;
            }
            vertesLevels[i] = lv;
        }
        if (negativeVertesNum == 0) {
            return null;
        }

        int firstInsideIndex = -1;
        for (int i = 0; i < vertesLevels.length; i++) {
            if (vertesLevels[i] < 0 && vertesLevels[(i + 2) % 3] >= 0) {
                firstInsideIndex = i;
                break;
            }
        }

        double[][] polygonVertesCoords = new double[2 + negativeVertesNum][];
        polygonVertesCoords[0] = edge.getStartCoord();
        polygonVertesCoords[1] = edge.getEndCoord();
        for (int i = 0; i < negativeVertesNum; i++) {
            polygonVertesCoords[2 + i] = cell.getVertexCoord((firstInsideIndex + i) % 3);
        }
        PolygonIntegrateUnit result = new PolygonIntegrateUnit();
        result.setVertesCoords(polygonVertesCoords);
        return result;

    }
}
