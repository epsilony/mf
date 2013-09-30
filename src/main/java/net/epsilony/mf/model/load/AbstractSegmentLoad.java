/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.model.load;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author epsilon
 */
public abstract class AbstractSegmentLoad implements SegmentLoad {

    protected Segment segment;
    protected double parameter;

    @Override
    public void setSegment(Segment seg) {
        this.segment = seg;
    }

    @Override
    public void setParameter(double parm) {
        this.parameter = parm;
    }
}
