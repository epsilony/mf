/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractSegmentLoad extends AbstractLoad implements SegmentLoad {

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
