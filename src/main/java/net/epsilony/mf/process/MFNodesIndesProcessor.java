/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFBoundary;
import net.epsilony.mf.geomodel.MFLineBnd;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.MFNodeBnd;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.tb.analysis.Dimensional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesIndesProcessor implements Dimensional {

    public static Logger logger = LoggerFactory.getLogger(MFNodesIndesProcessor.class);
    private List<MFNode> allGeomNodes;
    private List<? extends MFBoundary> boundaries;
    private List<MFNode> spaceNodes;
    private List<MFBoundaryIntegratePoint> dirichletTasks;
    private List<MFNode> extraLagDirichletNodes;
    private List<MFNode> allProcessNodes;
    private boolean applyDirichletByLagrange;
    private int dimension;

    public void setBoundaries(List<? extends MFBoundary> boundaries) {
        this.boundaries = (List<MFLineBnd>) boundaries;
    }

    public List<? extends MFBoundary> getBoundaries() {
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
        processSpaceNodes();
        processGeomNodes();
        processLagrangeAndExtraDirichletNodes();
    }

    private void processSpaceNodes() {
        int asmIndex = 0;
        for (MFNode nd : spaceNodes) {
            nd.setAssemblyIndex(asmIndex++);
        }
    }

    private void processGeomNodes() {
        int asmIndex = spaceNodes.get(spaceNodes.size() - 1).getAssemblyIndex() + 1;
        allGeomNodes = new LinkedList<>(spaceNodes);
        if (null == boundaries) {
            return;
        }
        switch (dimension) {
            case 1:
                for (MFBoundary bnd : boundaries) {
                    MFNodeBnd nodeBnd = (MFNodeBnd) bnd;
                    MFNode node = nodeBnd.getNode();
                    node.setAssemblyIndex(asmIndex++);
                    allGeomNodes.add(node);
                }
                break;
            case 2:
                for (MFBoundary bnd : boundaries) {
                    MFLineBnd lineBnd = (MFLineBnd) bnd;
                    MFNode nd = (MFNode) lineBnd.getLine().getStart();
                    nd.setAssemblyIndex(asmIndex++);
                    allGeomNodes.add(nd);
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void processLagrangeAndExtraDirichletNodes() {

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
        switch (dimension) {
            case 1:
                process1DExtraLagDiri();
                break;
            case 2:
                process2DExtraLagDiri();
                break;
            default:
                throw new IllegalStateException();
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

    private void process1DExtraLagDiri() {
        int nodeIndex = allGeomNodes.get(allGeomNodes.size() - 1).getAssemblyIndex() + 1;
        extraLagDirichletNodes = new LinkedList<>();
        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            MFNodeBnd bnd = (MFNodeBnd) qp.getBoundary();
            MFNode node = bnd.getNode();
            node.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = nodeIndex;

        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            MFNodeBnd bnd = (MFNodeBnd) qp.getBoundary();
            MFNode node = bnd.getNode();
            if (node.getLagrangeAssemblyIndex() < 0) {
                node.setLagrangeAssemblyIndex(lagIndex++);
                if (node.getAssemblyIndex() < 0) {
                    extraLagDirichletNodes.add(node);
                }
            }
        }
    }

    private void process2DExtraLagDiri() {
        int asmIndex = allGeomNodes.get(allGeomNodes.size() - 1).getAssemblyIndex() + 1;
        extraLagDirichletNodes = new LinkedList<>();
        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            MFLineBnd lineBnd = (MFLineBnd) qp.getBoundary();
            MFNode start = (MFNode) lineBnd.getLine().getStart();
            MFNode end = (MFNode) lineBnd.getLine().getEnd();
            start.setLagrangeAssemblyIndex(-1);
            end.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = asmIndex;

        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            MFLineBnd lineBnd = (MFLineBnd) qp.getBoundary();
            MFNode node = (MFNode) lineBnd.getLine().getStart();
            for (int i = 0; i < 2; i++) {
                if (node.getLagrangeAssemblyIndex() < 0) {
                    node.setLagrangeAssemblyIndex(lagIndex++);
                    if (node.getAssemblyIndex() < 0) {
                        extraLagDirichletNodes.add(node);
                    }
                }

                node = (MFNode) lineBnd.getLine().getEnd();
            }
        }
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

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
