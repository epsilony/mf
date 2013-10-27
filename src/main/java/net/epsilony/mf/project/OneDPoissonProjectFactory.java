/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.Arrays;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.ChainModelFactory;
import net.epsilony.mf.model.ChainPhM;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.Assemblers;
import net.epsilony.mf.process.assembler.PoissonVolumeAssembler;
import net.epsilony.mf.process.integrate.ChainIntegrateTaskFactory;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Node;
import static net.epsilony.mf.project.MFProjectKey.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoissonProjectFactory implements Factory<MFProject> {

    public static final double DEFAULT_INFLUENCE_RADIUS_RATIO = 3.5;
    public static final int DEFAULT_NODES_NUM = 11;
    ChainIntegrateTaskFactory integrateTaskFactory = new ChainIntegrateTaskFactory();
    Assembler assembler = new PoissonVolumeAssembler();
    double start = 0;
    double end = 1;
    NodeLoad startLoad, endLoad;
    SegmentLoad volumeLoad;
    SimpMFProject result;
    int nodesNum = DEFAULT_NODES_NUM;
    double influenceRadRatio = DEFAULT_INFLUENCE_RADIUS_RATIO;

    @Override
    public MFProject produce() {
        result = new SimpMFProject();

        result.put(VALUE_DIMENSION, 1);

        result.put(SPATIAL_DIMENSION, 1);

        result.put(ASSEMBLERS_GROUP, Assemblers.poissonLagrangle());

        result.put(INFLUENCE_RADIUS_CALCULATOR, new ConstantInfluenceRadiusCalculator(getInfluenceRadius()));

        result.put(ANALYSIS_MODEL, genAnalysisModel());

        result.put(INTEGRATE_TASKS, genIntegrateTask());

        return result;
    }

    public double getInfluenceRadius() {
        double radials = (end - start) / (nodesNum - 1) * influenceRadRatio;
        if (radials <= 0) {
            throw new IllegalStateException();
        }
        return radials;
    }

    public AnalysisModel genAnalysisModel() {
        Chain chain = Chain.byNodesChain(Arrays.asList(new Node(new double[]{start, 0}), new Node(new double[]{end, 0})), false);
        ChainPhM chainPhM = new ChainPhM();
        chainPhM.setChain(chain);
        chainPhM.setVolumeLoad(volumeLoad);
        chainPhM.getLoadMap().put(chain.getHead().getStart(), startLoad);
        chainPhM.getLoadMap().put(chain.getLast().getStart(), endLoad);

        ChainModelFactory chainModelFactory = new ChainModelFactory();
        chainModelFactory.setChainPhM(chainPhM);
        chainModelFactory.setFractionLengthCap((end - start) / (nodesNum - 1.1));

        return chainModelFactory.produce();

    }

    private MFIntegrateTask genIntegrateTask() {
        integrateTaskFactory.setChainAnalysisModel((AnalysisModel) result.getDatas().get(ANALYSIS_MODEL));
        return integrateTaskFactory.produce();
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public NodeLoad getStartLoad() {
        return startLoad;
    }

    public void setStartLoad(NodeLoad startLoad) {
        this.startLoad = startLoad;
    }

    public NodeLoad getEndLoad() {
        return endLoad;
    }

    public void setEndLoad(NodeLoad endLoad) {
        this.endLoad = endLoad;
    }

    public SegmentLoad getVolumeLoad() {
        return volumeLoad;
    }

    public void setVolumeLoad(SegmentLoad volumeLoad) {
        this.volumeLoad = volumeLoad;
    }

    public int getNodesNum() {
        return nodesNum;
    }

    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    public int getQuadratureDegree() {
        return integrateTaskFactory.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        integrateTaskFactory.setQuadratureDegree(quadratureDegree);
    }

    public double getInfluenceRadRatio() {
        return influenceRadRatio;
    }

    public void setInfluenceRadRatio(double influenceRadRatio) {
        this.influenceRadRatio = influenceRadRatio;
    }
}
