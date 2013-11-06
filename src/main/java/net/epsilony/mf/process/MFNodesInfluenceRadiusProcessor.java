/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.process;

import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
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
    private List<GeomUnit> boundaries;
    private List<MFNode> spaceNodes;
    private SupportDomainSearcherFactory supportDomainSearcherFactory;
    private double maxNodesInfluenceRadius;
    private int dimension;

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public void setAllNodes(List<MFNode> allNodes) {
        this.allNodes = allNodes;
    }

    public void setBoundaries(List<GeomUnit> boundaries) {
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
        supportDomainSearcherFactory.setBoundarySegments((List) boundaries);

        influenceRadiusCalculator.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        switch (dimension) {
            case 1:
                process1D();
                break;
            case 2:
                process2D();
                break;
            default:
                throw new IllegalStateException();
        }
        maxNodesInfluenceRadius = MFNode.calcMaxInfluenceRadius(allNodes);

        logger.info("nodes influence radius processor processed");
        logger.info("boundaries num: {}", boundaries == null ? 0 : boundaries.size());
        logger.info("max nodes influence radius: {}", maxNodesInfluenceRadius);
    }

    private void process1D() {
        for (MFNode nd : allNodes) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), null);
            nd.setInfluenceRadius(rad);
        }
    }

    private void process2D() {
        for (MFNode nd : spaceNodes) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), null);
            nd.setInfluenceRadius(rad);
        }

        if (null != boundaries) {
            for (GeomUnit bnd : boundaries) {
                MFNode nd = (MFNode) ((Line) bnd).getStart();
                double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), bnd);
                nd.setInfluenceRadius(rad);
            }
        }
    }

    public SupportDomainSearcherFactory getSupportDomainSearcherFactory() {
        return supportDomainSearcherFactory;
    }

    public double getMaxNodesInfluenceRadius() {
        return maxNodesInfluenceRadius;
    }
}
