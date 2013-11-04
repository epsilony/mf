/* (c) Copyright by Man YUAN */

package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentSubdomain implements MFSubdomain{
    Segment segment;

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }
    
}
