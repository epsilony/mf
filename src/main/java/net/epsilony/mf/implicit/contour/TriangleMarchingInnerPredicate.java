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

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleMarchingInnerPredicate implements Predicate<MFCell> {
    private ToDoubleFunction<double[]> levelFunction;

    public TriangleMarchingInnerPredicate() {
    }

    public TriangleMarchingInnerPredicate(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

    @Override
    public boolean test(MFCell cell) {
        for (MFLine line : cell.getVertexEdge(0)) {
            double levelValue = levelFunction.applyAsDouble(line.getStartCoord());
            if (levelValue > 0) {
                return false;
            }
        }
        return true;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

}
