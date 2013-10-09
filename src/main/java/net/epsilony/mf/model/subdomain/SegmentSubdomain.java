/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentSubdomain implements MFSubdomain {

    Segment segment;
    double startParameter = 0;
    double endParameter = 1;

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
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
