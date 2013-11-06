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

package net.epsilony.mf.implicit;

import net.epsilony.tb.implicit.CircleLevelSet;
import java.awt.geom.Rectangle2D;
import java.util.List;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleWithHolesTest {
//
//    public RectangleWithHolesTest() {
//    }
//
//    @Test
//    public void testPerimeter() {
//        RectangleWithHoles rectangleWithHole = genInstance();
//        List<Segment2DQuadraturePoint> boundaryQuadraturePoints = rectangleWithHole.getBoundaryQuadraturePoints();
//        double act = 0;
//        for (QuadraturePoint qp : boundaryQuadraturePoints) {
//            act += qp.weight;
//        }
//        Rectangle2D rect = rectangleWithHole.getRectangle();
//
//        double exp = 0;
//        exp += rect.getWidth() * 2 + rect.getHeight() * 2;
//        for (CircleLevelSet circle : rectangleWithHole.getHoles()) {
//            exp += circle.getRadius() * 2 * Math.PI;
//        }
//
//        assertEquals(exp, act, 1e-11);
////        System.out.println("exp = " + exp);
////        System.out.println("act = " + act);
//    }
//
//    RectangleWithHoles genInstance() {
//        Rectangle2D rectangle = new Rectangle2D.Double(10, 20, 100, 60);
//        double holeRadius = 5;
//        double holeDistance = 2;
//        double triangleSize = 1;
//        double spaceNodesExtention = triangleSize * 2;
//        RectangleWithHoles rectangleWithHoles = new RectangleWithHoles(rectangle, holeRadius, holeDistance);
//        rectangleWithHoles.setTriangleSize(triangleSize);
//        rectangleWithHoles.setSpaceNodesExtension(spaceNodesExtention);
//        rectangleWithHoles.prepare();
//        return rectangleWithHoles;
//    }
}