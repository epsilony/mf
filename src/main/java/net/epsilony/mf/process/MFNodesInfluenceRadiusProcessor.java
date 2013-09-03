/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tb.solid.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesInfluenceRadiusProcessor {

    public static Logger logger = LoggerFactory.getLogger(MFNodesInfluenceRadiusProcessor.class);
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

    public void process() {
        logger.info("start calculating nodes influence radius");
        logger.info("influence radius calculator: {}", influenceRadiusCalculator);
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

        logger.info("nodes influence radius processor processed");
        logger.info("boundaries num: {}", boundaries.size());
        logger.info("max nodes influence radius: {}", maxNodesInfluenceRadius);
    }

    public SupportDomainSearcherFactory getSupportDomainSearcherFactory() {
        return supportDomainSearcherFactory;
    }

    public double getMaxNodesInfluenceRadius() {
        return maxNodesInfluenceRadius;
    }
}
