/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFBoundary;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFNodeBnd;
import net.epsilony.mf.model.RawGeomModel;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.PoissonAssembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.OneDIntegrateTask;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.SimpMFBoundaryIntegratePoint;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoisson implements MFProject {

    private double influenceRadius;
    OneDIntegrateTask integrateTask = new OneDIntegrateTask();
    double nodesDistanceUpperBound;
    MFNode startNode = new MFNode(new double[1]);
    MFNode endNode = new MFNode(new double[1]);
    MFNodeBnd[] bnds = new MFNodeBnd[]{new MFNodeBnd(startNode), new MFNodeBnd(endNode)};
    double[][] loads = new double[2][];
    boolean[][] loadsValidity = new boolean[2][];
    private static final int START_IDX = 0, END_IDX = 1;

    public double getNodesDistanceUpperBound() {
        return nodesDistanceUpperBound;
    }

    public void setNodesDistanceUpperBound(double nodesDistanceUpperBound) {
        this.nodesDistanceUpperBound = nodesDistanceUpperBound;
    }

    public void setVolumeLoadFunction(UnivariateFunction volumeFunction) {
        integrateTask.setVolumeFunction(volumeFunction);
    }

    public void setQuadratureDegree(int degree) {
        integrateTask.setDegree(degree);
    }

    public void setStart(double start) {
        this.startNode.getCoord()[0] = start;
        integrateTask.setStart(start);
    }

    public void setEnd(double end) {
        this.endNode.getCoord()[0] = end;
        integrateTask.setEnd(end);
    }

    public void setIntegrateDomainUpperBound(double integrateDomainUpperBound) {
        integrateTask.setIntegrateDomainUpperBound(integrateDomainUpperBound);
    }

    public void setBoudaryLoadAtStart(double[] load, boolean[] validity) {
        loads[START_IDX] = load;
        loadsValidity[START_IDX] = validity;
    }

    public void setBoudaryLoadAtEnd(double[] load, boolean[] validity) {
        loads[END_IDX] = load;
        loadsValidity[END_IDX] = validity;
    }

    @Override
    public MFSolver getMFSolver() {
        return new RcmSolver();
    }

    @Override
    public Assembler getAssembler() {
        return new PoissonAssembler();
    }

    @Override
    public MFIntegrateTask getMFIntegrateTask() {
        List<MFBoundaryIntegratePoint> diriPts = new LinkedList<>();
        List<MFBoundaryIntegratePoint> neuPts = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            SimpMFBoundaryIntegratePoint pt = new SimpMFBoundaryIntegratePoint();
            pt.setDimension(1);
            pt.setLoad(loads[i]);
            pt.setLoadValidity(loadsValidity[i]);
            MFNode bndNode = i == START_IDX ? startNode : endNode;
            pt.setCoord(bndNode.getCoord());
            pt.setWeight(1);
            pt.setBoundary(bnds[i]);

            if (loadsValidity[i] != null) {
                diriPts.add(pt);
            } else {
                neuPts.add(pt);
            }
        }
        integrateTask.setDirichletTasks(diriPts);
        integrateTask.setNeumannTasks(neuPts);
        return integrateTask;
    }

    @Override
    public AnalysisModel getModel() {
        RawGeomModel md = new RawGeomModel();
        md.setDimension(1);
        md.setBoundaries(Arrays.asList(bnds));
        md.setSpaceNodes(genSpaceNodes());
        return md;
    }

    private ArrayList<MFNode> genSpaceNodes() {
        double start = startNode.getCoord()[0];

        int spaceNodesNum = getNodesNum();
        double nodesDist = getNodesDistance();
        ArrayList<MFNode> spaceNodes = new ArrayList<>(spaceNodesNum);
        for (int i = 0; i < spaceNodesNum; i++) {
            double x = start + (i + 1) * nodesDist;
            spaceNodes.add(new MFNode(new double[]{x}));
        }
        return spaceNodes;
    }

    public int getNodesNum() {
        double start = startNode.getCoord()[0];
        double end = endNode.getCoord()[0];
        int spaceNodesNum = (int) Math.ceil((end - start) / nodesDistanceUpperBound) - 1;
        return spaceNodesNum;
    }

    public double getNodesDistance() {
        double start = startNode.getCoord()[0];
        double end = endNode.getCoord()[0];
        double nodesDist = (end - start) / (getNodesNum() + 1);
        return nodesDist;
    }

    @Override
    public MFShapeFunction getShapeFunction() {
        return new MLS();
    }

    @Override
    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return new ConstantInfluenceRadiusCalculator(influenceRadius);
    }

    public void setInfluenceRadius(double influenceRadius) {
        this.influenceRadius = influenceRadius;
    }

    public double getInfluenceRadius() {
        return influenceRadius;
    }

    public int getIntegrateDomainNum() {
        return integrateTask.getIntegrateDomainNum();
    }

    public double getIntegrateDomainLength() {
        return integrateTask.getIntegrateDomainLength();
    }

    @Override
    public int getDimension() {
        return 1;
    }
}
