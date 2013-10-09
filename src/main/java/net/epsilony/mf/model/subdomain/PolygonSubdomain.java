/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PolygonSubdomain implements MFSubdomain {

    double[] lineParameters;
    Line[] lines;
    double[][] vertexCoords;

    public PolygonSubdomain(int size) {
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
}
