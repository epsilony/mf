/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.project.sample.TimoshenkoBeamProjectFactory;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.MFIntegratePoint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.integrate.MFSimpIntegrator;
import net.epsilony.mf.process.MFProcessor;
import net.epsilony.mf.process.Mixer;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.synchron.SynchronizedIterator;
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
    protected Assembler<?> assembler;
    protected LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
//    private List<MFQuadraturePoint<QuadraturePoint>> volumeProcessPoints;
//    private List<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletProcessPoints;
//    private List<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannProcessPoints;
    SynchronizedIterator<MFIntegratePoint> volumeIteratorWrapper;
    SynchronizedIterator<MFBoundaryIntegratePoint> neumannIteratorWrapper;
    SynchronizedIterator<MFBoundaryIntegratePoint> dirichletIteratorWrapper;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;
    private ProcessResult processResult;
    private MFSolver solver = new RcmSolver();

    private List<MFSimpIntegrator> produceRunnables() {
        int coreNum = getRunnableNum();
        List<MFSimpIntegrator> result = new ArrayList<>(coreNum);
        for (int i = 0; i < coreNum; i++) {
            MFSimpIntegrator runnable = produceRunnable();
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
        volumeIteratorWrapper = mfQuadratureTask.volumeTasks();
        neumannIteratorWrapper = mfQuadratureTask.neumannTasks();
        dirichletIteratorWrapper = mfQuadratureTask.dirichletTasks();
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
        SynchronizedIterator<MFBoundaryIntegratePoint> dirichletTasks = mfQuadratureTask.dirichletTasks();
        for (MFBoundaryIntegratePoint qp = dirichletTasks.nextItem(); qp != null; qp = dirichletTasks.nextItem()) {
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
            for (MFBoundaryIntegratePoint qp = dirichletTasks.nextItem(); qp != null; qp = dirichletTasks.nextItem()) {
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
        result.setShapeFunction(shapeFunction.produceAClone());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        result.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        return result;
    }

    @Override
    public MFIntegrateTask getMFQuadratureTask() {
        return mfQuadratureTask;
    }

    @Override
    public void setMFQuadratureTask(MFIntegrateTask mfQuadratureTask) {
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
        return assembler.getDimension();
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
        mixer.setShapeFunction(shapeFunction.produceAClone());
        mixer.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        mixer.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        return mixer;
    }

    private MFSimpIntegrator produceRunnable() {
        Assembler produceAssembler = produceAssembler();
        Mixer mixer = produceMixer();
        MFSimpIntegrator runnable = new MFSimpIntegrator();
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
        return assembler.produceAClone();
    }

    private LinearLagrangeDirichletProcessor produceLagProcessor() {
        return lagProcessor.produceAClone();
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
