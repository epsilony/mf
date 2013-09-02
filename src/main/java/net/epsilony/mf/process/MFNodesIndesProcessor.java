/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesIndesProcessor {

    private List<MFNode> allGeomNodes;
    private List<Segment> boundaries;
    private List<MFNode> spaceNodes;
    private List<MFBoundaryIntegratePoint> dirichletTasks;
    private List<MFNode> extraLagDirichletNodes;
    private List<MFNode> allProcessNodes;
    private boolean applyDirichletByLagrange;

    public void setAllGeomNodes(List<MFNode> allGeomNodes) {
        this.allGeomNodes = allGeomNodes;
    }

    public void setBoundaries(List<Segment> boundaries) {
        this.boundaries = boundaries;
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

        if (null != boundaries) {
            for (Segment seg : boundaries) {
                MFNode nd = (MFNode) seg.getStart();
                nd.setAssemblyIndex(nodeIndex++);
            }
        }


        if (nodeIndex != allGeomNodes.size()) {
            throw new IllegalStateException();
        }

        extraLagDirichletNodes = null;
        if (!applyDirichletByLagrange) {
            allProcessNodes = allGeomNodes;
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
                }
                if (node.getId() < 0) {
                    node.setId(nodeIndex++);
                    extraLagDirichletNodes.add(node);
                }
                node = (MFNode) qp.getBoundary().getEnd();
            }
        }

        allProcessNodes = new ArrayList(allGeomNodes.size() + extraLagDirichletNodes.size());
        allProcessNodes.addAll(allGeomNodes);
        allProcessNodes.addAll(extraLagDirichletNodes);
    }

    public List<MFNode> getExtraLagDirichletNodes() {
        return extraLagDirichletNodes;
    }

    public List<MFNode> getAllProcessNodes() {
        return allProcessNodes;
    }
}
