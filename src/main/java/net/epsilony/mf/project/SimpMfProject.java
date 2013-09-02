/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.project.sample.TimoshenkoBeamProjectFactory;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.integrate.SimpMFIntegrator;
import net.epsilony.mf.process.MFProcessor;
import net.epsilony.mf.process.Mixer;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorCore;
import net.epsilony.mf.process.integrate.SimpMFIntegrateCore;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMfProject implements MFProject {

    public static final Logger logger = LoggerFactory.getLogger(MFProcessor.class);
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;
    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    public static final boolean DEFAULT_ENABLE_MULTITHREAD = true;
    //
    protected MFIntegrateTask mfQuadratureTask;
    protected GeomModel2D model;
    protected List<MFNode> extraLagDirichletNodes;
    protected MFShapeFunction shapeFunction = new MLS();
    protected Assembler assembler;
    protected LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
    private List<MFIntegratePoint> volumeProcessPoints;
    private List<MFBoundaryIntegratePoint> dirichletProcessPoints;
    private List<MFBoundaryIntegratePoint> neumannProcessPoints;
    SynchronizedIterator<MFIntegratePoint> volumeIteratorWrapper;
    SynchronizedIterator<MFBoundaryIntegratePoint> neumannIteratorWrapper;
    SynchronizedIterator<MFBoundaryIntegratePoint> dirichletIteratorWrapper;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;
    private ProcessResult processResult;
    private MFSolver solver = new RcmSolver();

    private List<MFIntegrator> produceIntegrators() {
        int coreNum = getRunnableNum();
        volumeIteratorWrapper = new SynchronizedIterator<>(volumeProcessPoints.iterator(), volumeProcessPoints.size());
        dirichletIteratorWrapper = new SynchronizedIterator<>(dirichletProcessPoints.iterator(), dirichletProcessPoints.size());
        neumannIteratorWrapper = new SynchronizedIterator<>(neumannProcessPoints.iterator(), neumannProcessPoints.size());
        List<MFIntegrator> result = new ArrayList<>(coreNum);
        for (int i = 0; i < coreNum; i++) {
            MFIntegrator runnable = produceIntegrator();
            result.add(runnable);
        }
        return result;
    }

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
        prepareProcessIteratorWrappers();

        prepareProcessNodesDatas();

        prepareAssembler();
    }

    private void prepareProcessIteratorWrappers() {
        volumeProcessPoints = mfQuadratureTask.volumeTasks();
        neumannProcessPoints = mfQuadratureTask.neumannTasks();
        dirichletProcessPoints = mfQuadratureTask.dirichletTasks();
    }

    private void prepareProcessNodesDatas() {
        int nodeIndex = 0;
        for (MFNode nd : model.getSpaceNodes()) {
            nd.setAssemblyIndex(nodeIndex++);
        }

        if (null != model.getPolygon()) {
            for (Segment seg : model.getPolygon()) {
                MFNode nd = (MFNode) seg.getStart();
                nd.setAssemblyIndex(nodeIndex++);
            }
        }


        if (nodeIndex != model.getAllNodes().size()) {
            throw new IllegalStateException();
        }
        List<MFBoundaryIntegratePoint> dirichletTasks = mfQuadratureTask.dirichletTasks();
        for (MFBoundaryIntegratePoint qp : dirichletTasks) {
            Segment segment = qp.getBoundary();
            MFNode start = (MFNode) segment.getStart();
            MFNode end = (MFNode) segment.getEnd();
            start.setLagrangeAssemblyIndex(-1);
            end.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = nodeIndex;
        extraLagDirichletNodes = new LinkedList<>();
        dirichletTasks = mfQuadratureTask.dirichletTasks();
        if (isAssemblyDirichletByLagrange()) {
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
        }
    }

    private void prepareAssembler() {
        assembler.setNodesNum(model.getAllNodes().size());
        boolean dense = model.getAllNodes().size() <= DENSE_MATRIC_SIZE_THRESHOLD;
        assembler.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor();
            int dirichletNodesSize = LinearLagrangeDirichletProcessor.calcLagrangeNodesNum(model.getAllNodes());
            dirichletNodesSize += LinearLagrangeDirichletProcessor.calcLagrangeNodesNum(extraLagDirichletNodes);
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
        result.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        result.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        return result;
    }

    @Override
    public MFIntegrateTask getMFQuadratureTask() {
        return mfQuadratureTask;
    }

    public void setMFQuadratureTask(MFIntegrateTask mfQuadratureTask) {
        this.mfQuadratureTask = mfQuadratureTask;
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

    private MFProcessor genProcessor() {
        prepare();
        MFProcessor result = new MFProcessor();
        result.setRunnables(produceIntegrators());
        result.setModelNodes(getModelNodes());
        result.setExtraLagNodes(getExtraLagNodes());
        return result;
    }

    private Mixer produceMixer() {
        Mixer mixer = new Mixer();
        mixer.setShapeFunction(SerializationUtils.clone(shapeFunction));
        mixer.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        mixer.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        return mixer;
    }

    private MFIntegrator produceIntegrator() {

        Assembler produceAssembler = produceAssembler();
        Mixer mixer = produceMixer();
        MFIntegrator runnable = new SimpMFIntegrator();
        MFIntegratorCore core = new SimpMFIntegrateCore();
        runnable.setIntegrateCore(core);
        core.setAssembler(produceAssembler);
        core.setMixer(mixer);
        runnable.setVolumeIterator(volumeIteratorWrapper);
        runnable.setDirichletIterator(dirichletIteratorWrapper);
        runnable.setNeumannIterator(neumannIteratorWrapper);
        return runnable;
    }

    private int getRunnableNum() {
        return enableMultiThread ? Runtime.getRuntime().availableProcessors() : 1;
    }

    private Assembler produceAssembler() {
        Assembler clone = SerializationUtils.clone(assembler);
        clone.prepare();
        return clone;
    }

    private LinearLagrangeDirichletProcessor produceLagProcessor() {
        return SerializationUtils.clone(lagProcessor);
    }

    public List<MFNode> getModelNodes() {
        return model.getAllNodes();
    }

    public List<MFNode> getExtraLagNodes() {
        return extraLagDirichletNodes;
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
        MFProcessor processor = genProcessor();
        processor.process();
        processResult = processor.getProcessResult();
    }

    public void solve() {
        solver.setProcessResult(processResult);
        solver.solve();
    }

    @Override
    public ProcessResult getProcessResult() {
        return processResult;
    }
}
