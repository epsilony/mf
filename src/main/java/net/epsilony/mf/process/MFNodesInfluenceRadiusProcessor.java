/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesInfluenceRadiusProcessor {

    private InfluenceRadiusCalculator influenceRadiusCalculator;
    private List<MFNode> allNodes;
    private List<Segment> boundaries;
    private List<MFNode> spaceNodes;
    private SupportDomainSearcherFactory supportDomainSearcherFactory;
    private double maxNodesInfluenceRadius;

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public void setAllNodes(List<MFNode> allNodes) {
        this.allNodes = allNodes;
    }

    public void setBoundaries(List<Segment> boundaries) {
        this.boundaries = boundaries;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void updateNodesInfluenceRadius() {
        supportDomainSearcherFactory = new SupportDomainSearcherFactory();
        supportDomainSearcherFactory.setAllMFNodes(allNodes);
        if (null != boundaries) {
            supportDomainSearcherFactory.setBoundaries(boundaries);
        } else {
            supportDomainSearcherFactory.setSegmentsSearcher(null);
        }

        influenceRadiusCalculator.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        for (MFNode nd : spaceNodes) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), null);
            nd.setInfluenceRadius(rad);
        }

        if (null != boundaries) {
            for (Segment seg : boundaries) {
                MFNode nd = (MFNode) seg.getStart();
                double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), seg);
                nd.setInfluenceRadius(rad);
            }
        }
        maxNodesInfluenceRadius = MFNode.calcMaxInfluenceRadius(allNodes);
    }

    public SupportDomainSearcherFactory getSupportDomainSearcherFactory() {
        return supportDomainSearcherFactory;
    }

    public double getMaxNodesInfluenceRadius() {
        return maxNodesInfluenceRadius;
    }
}
