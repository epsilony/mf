/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.pair.WithPair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainData {

    public List<MFNode> allNodes;
    public List<MFNode> visibleNodes;
    public List<Segment> segments;
    public List<WithPair<MFNode, Segment>> invisibleNodesAndBlockingSegments;
}
