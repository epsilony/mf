/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.Dimensional;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesIndesProcessor implements Dimensional {

    public static Logger logger = LoggerFactory.getLogger(MFNodesIndesProcessor.class);
    private List<MFNode> allGeomNodes;
    private List<MFNode> spaceNodes;
    private Collection<? extends GeomUnit> dirichletBnds;
    private List<MFNode> extraLagDirichletNodes;
    private List<MFNode> allProcessNodes;
    private boolean applyDirichletByLagrange;
    private int dimension;
    GeomUnit geomRoot;
    List<MFNode> boundaryNodes;

    public GeomUnit getGeomRoot() {
        return geomRoot;
    }

    public void setGeomRoot(GeomUnit geomRoot) {
        this.geomRoot = geomRoot;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void setDirichletBnds(Collection<? extends GeomUnit> dirichletBnds) {
        this.dirichletBnds = dirichletBnds;
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
        if (null == geomRoot) {
            return;
        }
        genBoundaryNodes();
        for (MFNode node : boundaryNodes) {
            node.setAssemblyIndex(asmIndex++);
            allGeomNodes.add(node);
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
        int asmIndex = allGeomNodes.get(allGeomNodes.size() - 1).getAssemblyIndex() + 1;
        extraLagDirichletNodes = new LinkedList<>();
        for (GeomUnit bnd : dirichletBnds) {
            MFNode node = (MFNode) bnd;
            node.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = asmIndex;

        for (GeomUnit bnd : dirichletBnds) {
            MFNode node = (MFNode) bnd;
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
        for (GeomUnit bnd : dirichletBnds) {
            Line line = (Line) bnd;
            MFNode start = (MFNode) line.getStart();
            MFNode end = (MFNode) line.getEnd();
            start.setLagrangeAssemblyIndex(-1);
            end.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = asmIndex;

        for (GeomUnit bnd : dirichletBnds) {
            Line line = (Line) bnd;
            MFNode node = (MFNode) line.getStart();
            for (int i = 0; i < 2; i++) {
                if (node.getLagrangeAssemblyIndex() < 0) {
                    node.setLagrangeAssemblyIndex(lagIndex++);
                    if (node.getAssemblyIndex() < 0) {
                        extraLagDirichletNodes.add(node);
                    }
                }

                node = (MFNode) line.getEnd();
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

    private void genBoundaryNodes() {
        boundaryNodes = new LinkedList<>();
        switch (dimension) {
            case 1:
                Chain chain = (Chain) geomRoot;
                boundaryNodes.add((MFNode) chain.getHead().getStart());
                boundaryNodes.add((MFNode) chain.getLast().getStart());
                break;
            case 2:
                Facet facet = (Facet) geomRoot;
                for (Segment seg : facet) {
                    boundaryNodes.add((MFNode) seg.getStart());
                }
                break;
            case 3:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException();
        }
    }
}
