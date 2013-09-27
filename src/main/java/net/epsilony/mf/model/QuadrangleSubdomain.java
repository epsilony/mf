/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleSubdomain implements MFSubdomain {

    double[][] vertes = new double[4][];
    Segment[] vertesAsStarts = new Segment[4];

    public void setVertex(int index, double[] vertex) {
        vertes[index] = vertex;
    }

    public double[] getVertex(int index) {
        return vertes[index];
    }

    public void setSegment(int index, Segment segment) {
        vertesAsStarts[index] = segment;
    }

    public Segment getSegment(int index) {
        return vertesAsStarts[index];
    }
}
