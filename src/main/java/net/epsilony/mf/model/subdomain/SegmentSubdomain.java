/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentSubdomain implements MFSubdomain {

    Segment startSegment;
    Segment endSegment;
    double startParameter = 0;
    double endParameter = 1;

    public Segment getStartSegment() {
        return startSegment;
    }

    public void setStartSegment(Segment startSegment) {
        this.startSegment = startSegment;
    }

    public Segment getEndSegment() {
        return endSegment;
    }

    public void setEndSegment(Segment endSegment) {
        this.endSegment = endSegment;
    }

    public double getStartParameter() {
        return startParameter;
    }

    public void setStartParameter(double startParameter) {
        this.startParameter = startParameter;
    }

    public double getEndParameter() {
        return endParameter;
    }

    public void setEndParameter(double endParameter) {
        this.endParameter = endParameter;
    }
}
