/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SegmentLoad extends MFLoad {

    void setSegment(Segment seg);

    void setParameter(double parm);
}
