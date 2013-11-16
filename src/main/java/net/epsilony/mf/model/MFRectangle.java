package net.epsilony.mf.model;

import static net.epsilony.mf.model.MFRectangleEdge.DOWN;
import static net.epsilony.mf.model.MFRectangleEdge.LEFT;
import static net.epsilony.mf.model.MFRectangleEdge.RIGHT;
import static net.epsilony.mf.model.MFRectangleEdge.UP;
import static net.epsilony.mf.model.MFRectangleEdge.values;

import java.util.EnumMap;
import java.util.Iterator;

import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MFRectangle {
    protected EnumMap<MFRectangleEdge, Line> edgeLineMap = new EnumMap<>(MFRectangleEdge.class);
    protected Facet facet;

    public MFRectangle() {
        initInnerModel();
    }

    private void initInnerModel() {
        double[][][] vertesCoords = new double[][][] { { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } } };
        facet = Facet.byCoordChains(vertesCoords, new MFNode());

        Iterator<Segment> bndIter = facet.iterator();
        for (MFRectangleEdge edge : values()) {
            edgeLineMap.put(edge, (Line) bndIter.next());
        }
    }

    public Facet getFacet() {
        return facet;
    }

    public Line getEdgeLine(MFRectangleEdge edge) {
        return edgeLineMap.get(edge);
    }

    public double[][] getVertexCoords() {
        double[][] result = new double[4][];
        int index = 0;
        for (MFRectangleEdge edge : MFRectangleEdge.values()) {
            result[index] = edgeLineMap.get(edge).getStartCoord();
            index++;
        }
        return result;
    }

    public double getEdgePosition(MFRectangleEdge edge) {
        Line line = edgeLineMap.get(edge);
        if (edge == LEFT || edge == RIGHT) {
            return line.getStart().getCoord()[0];
        } else {
            return line.getStart().getCoord()[1];
        }
    }

    public void setEdgePosition(MFRectangleEdge edge, double position) {
        Line line = edgeLineMap.get(edge);
        if (edge == LEFT || edge == RIGHT) {
            line.getStartCoord()[0] = position;
            line.getEndCoord()[0] = position;
        } else {
            line.getStartCoord()[1] = position;
            line.getEndCoord()[1] = position;
        }
    }

    public boolean isAvialable() {
        if (getEdgePosition(LEFT) >= getEdgePosition(RIGHT) || getEdgePosition(DOWN) >= getEdgePosition(UP)) {
            return false;
        }
        return true;
    }

    protected void checkRectangleParameters() {
        if (getEdgePosition(LEFT) >= getEdgePosition(RIGHT)) {
            throw new IllegalArgumentException(String.format("left (%f) should be less then right (%f)",
                    getEdgePosition(LEFT), getEdgePosition(RIGHT)));
        }
        if (getEdgePosition(DOWN) >= getEdgePosition(UP)) {
            throw new IllegalArgumentException(String.format("down (%f) should be less then up (%f)",
                    getEdgePosition(DOWN), getEdgePosition(UP)));
        }
    }

    public double getWidth() {
        return getEdgePosition(RIGHT) - getEdgePosition(LEFT);
    }

    public double getHeight() {
        return getEdgePosition(UP) - getEdgePosition(DOWN);
    }

    public MFRectangleEdge searchEdge(Line line) {
        double[] startCoord = line.getStartCoord();
        double[] endCoord = line.getEndCoord();
        if (startCoord[0] == endCoord[0]) {
            if (startCoord[1] == endCoord[1]) {
                throw new IllegalArgumentException();
            }
            if (startCoord[0] == getEdgePosition(LEFT)) {
                return LEFT;
            } else if (startCoord[0] == getEdgePosition(RIGHT)) {
                return RIGHT;
            } else {
                return null;
            }
        } else if (startCoord[1] == endCoord[1]) {
            if (startCoord[1] == getEdgePosition(DOWN)) {
                return DOWN;
            } else if (startCoord[1] == getEdgePosition(UP)) {
                return UP;
            } else {
                return null;
            }
        }
        return null;
    }
}
