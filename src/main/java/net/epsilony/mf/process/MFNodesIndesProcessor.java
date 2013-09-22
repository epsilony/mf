/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFBoundary;
import net.epsilony.mf.geomodel.MFLine;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.tb.solid.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesIndesProcessor {

    public static Logger logger = LoggerFactory.getLogger(MFNodesIndesProcessor.class);
    private List<MFNode> allGeomNodes;
    private List<MFLine> boundaries;
    private List<MFNode> spaceNodes;
    private List<MFBoundaryIntegratePoint> dirichletTasks;
    private List<MFNode> extraLagDirichletNodes;
    private List<MFNode> allProcessNodes;
    private boolean applyDirichletByLagrange;

    public void setBoundaries(List<? extends MFBoundary> boundaries) {
        this.boundaries = (List<MFLine>) boundaries;
    }

    public List<MFLine> getBoundaries() {
        return boundaries;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void setDirichletTasks(List<MFBoundaryIntegratePoint> dirichletTasks) {
        this.dirichletTasks = dirichletTasks;
    }

    public void setApplyDirichletByLagrange(boolean applyDirichletByLagrange) {
        this.applyDirichletByLagrange = applyDirichletByLagrange;
    }

    public void process() {
        int nodeIndex = 0;
        for (MFNode nd : spaceNodes) {
            nd.setAssemblyIndex(nodeIndex++);
        }

        allGeomNodes = new LinkedList<>(spaceNodes);
        if (null != boundaries) {
            for (Segment seg : boundaries) {
                MFNode nd = (MFNode) seg.getStart();
                nd.setAssemblyIndex(nodeIndex++);
                allGeomNodes.add(nd);
            }
        }

        extraLagDirichletNodes = null;
        if (!applyDirichletByLagrange) {
            allProcessNodes = allGeomNodes;
            logger.info("nodes indes processed");
            logger.info("(SPACE/ALL_GEOM/EXTRA_LAG/ALL_PROC)=({}, {}, null, {})",
                    spaceNodes.size(),
                    allGeomNodes.size(),
                    allProcessNodes.size());
            return;
        }
        extraLagDirichletNodes = new LinkedList<>();
        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            Segment segment = qp.getBoundary();
            MFNode start = (MFNode) segment.getStart();
            MFNode end = (MFNode) segment.getEnd();
            start.setLagrangeAssemblyIndex(-1);
            end.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = nodeIndex;



        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            MFNode node = (MFNode) qp.getBoundary().getStart();
            for (int i = 0; i < 2; i++) {
                int lagrangeAssemblyIndex = node.getLagrangeAssemblyIndex();
                if (lagrangeAssemblyIndex < 0) {
                    node.setLagrangeAssemblyIndex(lagIndex++);
                    if (node.getAssemblyIndex() < 0) {
                        extraLagDirichletNodes.add(node);
                    }
                }

                node = (MFNode) qp.getBoundary().getEnd();
            }
        }

        allProcessNodes = new ArrayList(allGeomNodes.size() + extraLagDirichletNodes.size());
        allProcessNodes.addAll(allGeomNodes);
        allProcessNodes.addAll(extraLagDirichletNodes);

        logger.info("nodes indes processed");
        logger.info("(SPACE/ALL_GEOM/EXTRA_LAG/ALL_PROC)=({}, {}, {}, {})",
                spaceNodes.size(),
                allGeomNodes.size(),
                extraLagDirichletNodes.size(),
                allProcessNodes.size());
    }

    public List<MFNode> getAllGeomNodes() {
        return allGeomNodes;
    }

    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public List<MFNode> getExtraLagDirichletNodes() {
        return extraLagDirichletNodes;
    }

    public List<MFNode> getAllProcessNodes() {
        return allProcessNodes;
    }
}
