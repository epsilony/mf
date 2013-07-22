/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MFProcessWorker;
import net.epsilony.mf.process.MFProcessor;
import net.epsilony.mf.process.Mixer;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.synchron.SynchronizedIteratorWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMfProject implements MFProject {

    protected MFQuadratureTask mfQuadratureTask;
    protected GeomModel2D model;
    protected List<MFNode> extraLagDirichletNodes;
    protected MFShapeFunction shapeFunction = new MLS();
    protected Assembler<?> assembler;
    protected LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
    public static final Logger logger = LoggerFactory.getLogger(MFProcessor.class);
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;
    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    public static final boolean DEFAULT_ENABLE_MULTITHREAD = true;
    private List<MFQuadraturePoint> volumeProcessPoints;
    private List<MFQuadraturePoint> dirichletProcessPoints;
    private List<MFQuadraturePoint> neumannProcessPoints;
    SynchronizedIteratorWrapper<MFQuadraturePoint> volumeIteratorWrapper;
    SynchronizedIteratorWrapper<MFQuadraturePoint> neumannIteratorWrapper;
    SynchronizedIteratorWrapper<MFQuadraturePoint> dirichletIteratorWrapper;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;
    private ProcessResult processResult;
    private MFSolver solver = new RcmSolver();

    private List<MFProcessWorker> produceRunnables() {
        int coreNum = getRunnableNum();
        List<MFProcessWorker> result = new ArrayList<>(coreNum);
        for (int i = 0; i < coreNum; i++) {
            MFProcessWorker runnable = produceRunnable();
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
        dirichletProcessPoints = mfQuadratureTask.dirichletTasks();
        neumannProcessPoints = mfQuadratureTask.neumannTasks();
        volumeIteratorWrapper = volumeProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(volumeProcessPoints.iterator());
        neumannIteratorWrapper = neumannProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(neumannProcessPoints.iterator());
        dirichletIteratorWrapper = dirichletProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(dirichletProcessPoints.iterator());
    }

    private void prepareProcessNodesDatas() {
        int index = 0;
        for (MFNode nd : model.getSpaceNodes()) {
            nd.setAssemblyIndex(index++);
        }

        if (null != model.getPolygon()) {
            for (Segment seg : model.getPolygon()) {
                MFNode nd = (MFNode) seg.getStart();
                nd.setAssemblyIndex(index++);
            }
        }

        extraLagDirichletNodes = new LinkedList<>();
        int nodeId = model.getAllNodes().size();
        if (isAssemblyDirichletByLagrange()) {
            for (MFQuadraturePoint qp : dirichletProcessPoints) {
                MFNode node = (MFNode) qp.segment.getStart();
                for (int i = 0; i < 2; i++) {
                    if (node.getLagrangeAssemblyIndex() < 0) {
                        node.setLagrangeAssemblyIndex(index++);
                    }
                    if (node.getId() < 0) {
                        node.setId(nodeId++);
                        extraLagDirichletNodes.add(node);
                    }
                    node = (MFNode) qp.segment.getEnd();
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
            int dirichletNodesSize = LinearLagrangeDirichletProcessor.calcDirichletNodesSize(model.getAllNodes());
            dirichletNodesSize += LinearLagrangeDirichletProcessor.calcDirichletNodesSize(extraLagDirichletNodes);
            LagrangeAssembler sL = (LagrangeAssembler) assembler;
            sL.setDirichletNodesNum(dirichletNodesSize);
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
        result.setShapeFunction(shapeFunction.synchronizeClone());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        result.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        return result;
    }

    @Override
    public MFQuadratureTask getMFQuadratureTask() {
        return mfQuadratureTask;
    }

    @Override
    public void setMFQuadratureTask(MFQuadratureTask mfQuadratureTask) {
        this.mfQuadratureTask = mfQuadratureTask;
    }

    @Override
    public GeomModel2D getModel() {
        return model;
    }

    @Override
    public void setModel(GeomModel2D model) {
        this.model = model;
    }

    @Override
    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    @Override
    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    @Override
    public Assembler<?> getAssembler() {
        return assembler;
    }

    @Override
    public void setAssembler(Assembler<?> assembler) {
        this.assembler = assembler;
    }

    public int getNodeValueDimension() {
        return assembler.getNodeValueDimension();
    }

    private MFProcessor genProcessor() {
        prepare();
        MFProcessor result = new MFProcessor();
        result.setRunnables(produceRunnables());
        result.setModelNodes(getModelNodes());
        result.setExtraLagNodes(getExtraLagNodes());
        return result;
    }

    private Mixer produceMixer() {
        Mixer mixer = new Mixer();
        mixer.setShapeFunction(shapeFunction.synchronizeClone());
        mixer.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        mixer.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        return mixer;
    }

    private MFProcessWorker produceRunnable() {
        Assembler produceAssembler = produceAssembler();
        Mixer mixer = produceMixer();
        MFProcessWorker runnable = new MFProcessWorker();
        runnable.setAssembler(produceAssembler);
        runnable.setMixer(mixer);
        runnable.setLagrangeProcessor(produceLagProcessor());
        runnable.setVolumeSynchronizedIterator(volumeIteratorWrapper);
        runnable.setDirichletSynchronizedIterator(dirichletIteratorWrapper);
        runnable.setNeumannSynchronizedIterator(neumannIteratorWrapper);
        return runnable;
    }

    private int getRunnableNum() {
        return enableMultiThread ? Runtime.getRuntime().availableProcessors() : 1;
    }

    private Assembler produceAssembler() {
        return assembler.synchronizeClone();
    }

    private LinearLagrangeDirichletProcessor produceLagProcessor() {
        return lagProcessor.synchronizeClone();
    }

    public List<MFNode> getModelNodes() {
        return model.getAllNodes();
    }

    public List<MFNode> getExtraLagNodes() {
        return extraLagDirichletNodes;
    }

    public static TimoshenkStandardProjectFactory genTimoshenkoProjectProcessFactory() {
        TimoshenkoAnalyticalBeam2D timoBeam =
                new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkStandardProjectFactory timoFactory = new TimoshenkStandardProjectFactory();
        timoFactory.setTimoBeam(timoBeam);
        timoFactory.setQuadrangleDegree(quadDegree);
        timoFactory.setQuadrangleDomainSize(quadDomainSize);
        timoFactory.setSegmentLengthUpperBound(quadDomainSize);
        timoFactory.setInfluenceRad(inflRads);
        timoFactory.setSpaceNodesGap(quadDomainSize);
        return timoFactory;
    }

    @Override
    public void setMFSolver(MFSolver solver) {
        this.solver = solver;
    }

    @Override
    public MFSolver getMFSolver() {
        return solver;
    }

    @Override
    public void process() {
        MFProcessor processor = genProcessor();
        processor.process();
        processResult = processor.getProcessResult();
    }

    @Override
    public void solve() {
        solver.setProcessResult(processResult);
        solver.solve();
    }

    @Override
    public ProcessResult getProcessResult() {
        return processResult;
    }
}
