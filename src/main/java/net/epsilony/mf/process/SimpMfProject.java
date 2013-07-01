/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.TimoshenkoAnalyticalBeam2D;
import net.epsilony.mf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
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

    MFQuadratureTask mfQuadratureTask;
    Model2D model;
    List<MFNode> extraLagDirichletNodes;
    MFShapeFunction shapeFunction = new MLS();
    Assembler<?> assembler;
    LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
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
    SupportDomainSearcherFactory supportDomainSearcherFactory;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;
    private double maxInfluenceRadius;

    public void setup(MFMechanicalProject project) {
        setModel(project.getModel());
        setMFQuadratureTask(project.getMFQuadratureTask());
        setShapeFunction(project.getShapeFunction());
        setAssembler(project.getAssembler());
    }

    public List<MFProcessWorker> produceRunnables() {
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

    public void prepare() {
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

    void prepareAssembler() {
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

    public boolean isAssemblyDirichletByLagrange() {
        return assembler instanceof LagrangeAssembler;
    }

    @Override
    public PostProcessor genPostProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(shapeFunction.synchronizeClone());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        result.setMaxInfluenceRad(maxInfluenceRadius);
        return result;
    }

    @Override
    public MFQuadratureTask getMFQuadratureTask() {
        return mfQuadratureTask;
    }

    public void setMFQuadratureTask(MFQuadratureTask mfQuadratureTask) {
        this.mfQuadratureTask = mfQuadratureTask;
    }

    @Override
    public Model2D getModel() {
        return model;
    }

    public void setModel(Model2D model) {
        this.model = model;
        supportDomainSearcherFactory = model.getSupportDomainSearcherFactory();
        maxInfluenceRadius = model.getMaxInfluenceRadius();
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
        return assembler.getNodeValueDimension();
    }

    @Override
    public MFProcessor genProcessor() {
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
        mixer.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        mixer.setMaxInfluenceRad(maxInfluenceRadius);
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

    public static TimoshenkoStandardTask genTimoshenkoProjectProcessFactory() {
        TimoshenkoAnalyticalBeam2D timoBeam =
                new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoStandardTask task =
                new TimoshenkoStandardTask(timoBeam, quadDomainSize, quadDomainSize, quadDegree);
        task.setInfluenceRad(inflRads);
        task.setSpaceNdsGap(quadDomainSize);
        return task;
    }

    public static void main(String[] args) {
        SimpMfProject processFactory = (SimpMfProject) genTimoshenkoProjectProcessFactory().produce();
        processFactory.setEnableMultiThread(false);
        MFProcessor process = processFactory.genProcessor();
        process.process();
        process.solve();
        PostProcessor pp = processFactory.genPostProcessor();
        double[] value = pp.value(new double[]{0.1, 0}, null);
        System.out.println("value = " + Arrays.toString(value));
    }
}
