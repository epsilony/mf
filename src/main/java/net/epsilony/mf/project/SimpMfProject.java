/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.project.sample.TimoshenkoBeamProjectFactory;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MFMixerFactory;
import net.epsilony.mf.process.MFNodesIndesProcessor;
import net.epsilony.mf.process.MFNodesInfluenceRadiusProcessor;
import net.epsilony.mf.process.MFIntegrateProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.RawMFIntegrateTask;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMfProject implements MFProject {

    public static final Logger logger = LoggerFactory.getLogger(MFIntegrateProcessor.class);
    //
    protected MFIntegrateTask mfIntegrateTask;
    protected GeomModel2D model;
    protected MFNodesInfluenceRadiusProcessor nodesInfluenceRadiusProcessor = new MFNodesInfluenceRadiusProcessor();
    protected MFShapeFunction shapeFunction = new MLS();
    protected Assembler assembler;
    protected LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
    boolean enableMultiThread = ProjectConstants.DEFAULT_ENABLE_MULTITHREAD;
    private ProcessResult processResult;
    private MFSolver solver = new RcmSolver();
    protected InfluenceRadiusCalculator influenceRadiusCalculator;
    protected MFMixerFactory mixerFactory = new MFMixerFactory();
    protected MFNodesIndesProcessor nodesIndesProcessor = new MFNodesIndesProcessor();

    public boolean isActuallyMultiThreadable() {
        if (!isEnableMultiThread()) {
            return false;
        }
        int coreNum = Runtime.getRuntime().availableProcessors();
        if (coreNum <= 1) {
            return false;
        }
        return true;
    }

    public boolean isEnableMultiThread() {
        return enableMultiThread;
    }

    public void setEnableMultiThread(boolean enableMultiThread) {
        this.enableMultiThread = enableMultiThread;
    }

    private void prepare() {

        prepareProcessNodesDatas();

        mixerFactory.setMaxNodesInfluenceRadius(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        mixerFactory.setShapeFunction(shapeFunction);
        mixerFactory.setSupportDomainSearcherFactory(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory());

        prepareAssembler();
    }

    private void prepareProcessNodesDatas() {
        nodesIndesProcessor.setAllNodes(model.getAllNodes());
        nodesIndesProcessor.setSpaceNodes(model.getSpaceNodes());
        nodesIndesProcessor.setBoundaries(model.getPolygon().getSegments());
        nodesIndesProcessor.setApplyDirichletByLagrange(isAssemblyDirichletByLagrange());
        nodesIndesProcessor.setDirichletTasks(mfIntegrateTask.dirichletTasks());
        nodesIndesProcessor.process();

        nodesInfluenceRadiusProcessor.setAllNodes(model.getAllNodes());
        nodesInfluenceRadiusProcessor.setSpaceNodes(model.getSpaceNodes());
        nodesInfluenceRadiusProcessor.setBoundaries(model.getPolygon().getSegments());
        nodesInfluenceRadiusProcessor.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        nodesInfluenceRadiusProcessor.updateNodesInfluenceRadius();
    }

    private void prepareAssembler() {
        assembler.setNodesNum(model.getAllNodes().size());
        boolean dense = model.getAllNodes().size() <= ProjectConstants.DENSE_MATRIC_SIZE_THRESHOLD;
        assembler.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor();
            int dirichletNodesSize = LinearLagrangeDirichletProcessor.calcLagrangeNodesNum(model.getAllNodes());
            dirichletNodesSize += LinearLagrangeDirichletProcessor.calcLagrangeNodesNum(nodesIndesProcessor.getExtraLagDirichletNodes());
            LagrangeAssembler sL = (LagrangeAssembler) assembler;
            sL.setLagrangeNodesSize(dirichletNodesSize);
        }
        assembler.prepare();
        logger.info(
                "prepared assembler: {}",
                assembler);
    }

    private boolean isAssemblyDirichletByLagrange() {
        return assembler instanceof LagrangeAssembler;
    }

    public PostProcessor genPostProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(SerializationUtils.clone(shapeFunction));
        result.setNodeValueDimension(getNodeValueDimension());
        result.setSupportDomainSearcher(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory().produce());
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        return result;
    }

    @Override
    public MFIntegrateTask getMFIntegrateTask() {
        return mfIntegrateTask;
    }

    public void setMFIntegrateTask(MFIntegrateTask task) {
        RawMFIntegrateTask copy = new RawMFIntegrateTask();
        copy.setVolumeTasks(task.volumeTasks());
        copy.setNeumannTasks(task.neumannTasks());
        copy.setDirichletTasks(task.dirichletTasks());
        this.mfIntegrateTask = copy;
    }

    @Override
    public GeomModel2D getModel() {
        return model;
    }

    public void setModel(GeomModel2D model) {
        this.model = model;
    }

    @Override
    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    @Override
    public Assembler getAssembler() {
        return assembler;
    }

    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    public int getNodeValueDimension() {
        return assembler.getDimension();
    }

    private MFIntegrateProcessor genProcessor() {
        prepare();
        MFIntegrateProcessor result = new MFIntegrateProcessor();
        result.setAssembler(assembler);
        result.setIntegrateTask(mfIntegrateTask);
        result.setMixerFactory(mixerFactory);
        result.setEnableMultiThread(enableMultiThread);
        return result;
    }

    public List<MFNode> getModelNodes() {
        return model.getAllNodes();
    }

    public static TimoshenkoBeamProjectFactory genTimoshenkoProjectProcessFactory() {
        TimoshenkoAnalyticalBeam2D timoBeam =
                new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoBeamProjectFactory timoFactory = new TimoshenkoBeamProjectFactory();
        timoFactory.setTimoBeam(timoBeam);
        timoFactory.setQuadrangleDegree(quadDegree);
        timoFactory.setQuadrangleDomainSize(quadDomainSize);
        timoFactory.setSegmentLengthUpperBound(quadDomainSize);
        timoFactory.setInfluenceRad(inflRads);
        timoFactory.setSpaceNodesGap(quadDomainSize);
        return timoFactory;
    }

    public void setMFSolver(MFSolver solver) {
        this.solver = solver;
    }

    @Override
    public MFSolver getMFSolver() {
        return solver;
    }

    public void process() {
        MFIntegrateProcessor processor = genProcessor();
        processor.process();
        processResult = processor.getProcessResult();
    }

    public void solve() {
        solver.setMainMatrix(processResult.getMainMatrix());
        solver.setMainVector(processResult.getGeneralForce());
        solver.setUpperSymmetric(processResult.isUpperSymmetric());
        List<MFNode> extraLagDirichletNodes = nodesIndesProcessor.getExtraLagDirichletNodes();
        int nodesSize = model.getAllNodes().size() + (extraLagDirichletNodes != null ? extraLagDirichletNodes.size() : 0);
        ArrayList<MFNode> nodes = new ArrayList<>(nodesSize);
        nodes.addAll(model.getAllNodes());
        if (nodesIndesProcessor != null) {
            nodes.addAll(extraLagDirichletNodes);
        }
        solver.setNodes(nodes);
        solver.solve();
    }

    @Override
    public ProcessResult getProcessResult() {
        return processResult;
    }

    @Override
    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }
}
