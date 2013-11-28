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

package net.epsilony.mf.process.integrate.unit;

import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PolygonIntegrateUnit extends AbstractWithDegreeIntegrateUnit {
    double[] lineParameters;
    Line[] lines;
    double[][] vertexCoords;
    GeomUnit embededIn;

    public PolygonIntegrateUnit(int size) {
        if (size < 3) {
            throw new IllegalArgumentException();
        }
        lineParameters = new double[size];
        lines = new Line[size];
        vertexCoords = new double[size][];
    }

    public int getVertesSize() {
        return vertexCoords.length;
    }

    public void setVertexCoord(int index, double[] coord) {
        vertexCoords[index] = coord;
    }

    public double[] getVertexCoord(int index) {
        return vertexCoords[index];
    }

    public void setVertexLine(int index, Line line) {
        lines[index] = line;
    }

    public Line getVertexLine(int index) {
        return lines[index];
    }

    public void setVertexLineParameter(int index, double parameter) {
        lineParameters[index] = parameter;
    }

    public double getVertexLineParameter(int index) {
        return lineParameters[index];
    }

    public GeomUnit getEmbededIn() {
        return embededIn;
    }

    public void setEmbededIn(GeomUnit embededIn) {
        this.embededIn = embededIn;
    }

    public double calcArea() {
        double area = 0;
        double[] start = vertexCoords[vertexCoords.length - 1];
        double[] end = vertexCoords[0];
        int i = 0;
        while (true) {
            area += start[0] * end[1] - start[1] * end[0];
            if (i >= vertexCoords.length) {
                break;
            }
            end = start;
            start = vertexCoords[i];
            i++;
        }
        area /= 2;
        return area;
    }
}
