/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.List;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.pair.WithPair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainData {

    public List<Node> allNodes;
    public List<Node> visibleNodes;
    public List<Segment> segments;
    public List<WithPair<Node, Segment>> invisibleNodesAndBlockingSegments;
}
