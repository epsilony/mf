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
package net.epsilony.mf.implicit.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.implicit.level.CircleLvFunction;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.util.function.RectangleToGridCoords;
import net.epsilony.mf.util.function.RectangleToGridCoords.ByNumRowsCols;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LevelFunctionApproximationTest {

    @Test
    public void circleLevelApproximate() {

        double radius = 10;
        double[] center = { -1, 3 };
        CircleLvFunction circle = new CircleLvFunction(center, radius);
        ToDoubleFunction<double[]> lvFunction = circle;

        MFRectangle nodesRect = new MFRectangle();
        double enlarge = 1.5;
        double[] drul = { center[1] - radius * enlarge, center[0] + radius * enlarge, center[1] + radius * enlarge,
                center[0] - radius * enlarge };
        nodesRect.setDrul(drul);

        int numRowsCols = 10;

        RectangleToGridCoords.ByNumRowsCols rectToCoords = new ByNumRowsCols();
        rectToCoords.setNumCols(numRowsCols);
        rectToCoords.setNumRows(numRowsCols);

        ArrayList<ArrayList<double[]>> coordsGrid = rectToCoords.apply(nodesRect);

        List<MFNode> nodes = coordsGrid.stream().flatMap(ArrayList::stream).map(MFNode::new)
                .collect(Collectors.toList());

        AnnotationConfigApplicationContext implicitProcessContext = new AnnotationConfigApplicationContext();

    }
}
