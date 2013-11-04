/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SubLineDomain implements MFSubdomain {

    Line startSegment;
    Line endSegment;
    double startParameter = 0;
    double endParameter = 1;

    public Segment getStartSegment() {
        return startSegment;
    }

    public void setStartSegment(Line startSegment) {
        this.startSegment = startSegment;
    }

    public Segment getEndSegment() {
        return endSegment;
    }

    public void setEndSegment(Line endSegment) {
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
