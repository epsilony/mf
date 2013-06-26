/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.TimoshenkoAnalyticalBeam2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.mf.process.assemblier.WeakformAssemblier;
import net.epsilony.mf.process.assemblier.WeakformLagrangeAssemblier;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.synchron.SynchronizedIteratorWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessorFactory implements Factory<WeakformProcessor> {

    WeakformQuadratureTask weakformQuadratureTask;
    InfluenceRadiusCalculator influenceRadiusCalculator;
    Model2D model;
    List<MFNode> extraLagDirichletNodes;
    MFShapeFunction shapeFunction = new MLS();
    WeakformAssemblier assemblier;
    LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
    public static final Logger logger = LoggerFactory.getLogger(WeakformProcessor.class);
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;
    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    public static final boolean DEFAULT_ENABLE_MULTITHREAD = true;
    ConstitutiveLaw constitutiveLaw;
    private List<WeakformQuadraturePoint> volumeProcessPoints;
    private List<WeakformQuadraturePoint> dirichletProcessPoints;
    private List<WeakformQuadraturePoint> neumannProcessPoints;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> volumeIteratorWrapper;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> neumannIteratorWrapper;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> dirichletIteratorWrapper;
    SupportDomainSearcherFactory supportDomainSearcherFactory;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;
    private double maxInfluenceRadius;

    public void setup(WeakformProject project) {
        setModel(project.getModel());
        setInfluenceRadiusCalculator(project.getInfluenceRadiusCalculator());
        setWeakformQuadratureTask(project.getWeakformQuadratureTask());
        setShapeFunction(project.getShapeFunction());
        setAssemblier(project.getAssemblier());
        setConstitutiveLaw(project.getConstitutiveLaw());
    }

    public List<WeakformProcessRunnable> produceRunnables() {
        int coreNum = getRunnableNum();
        List<WeakformProcessRunnable> result = new ArrayList<>(coreNum);
        for (int i = 0; i < coreNum; i++) {
            WeakformProcessRunnable runnable = produceRunnable();
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

        prepareSupportDomainSearcherFactoryWithoutInfluenceRadiusFilter();

        prepareProcessNodesDatas();

        supportDomainSearcherFactory.setFilterByInfluenceRad(true);

        prepareAssemblier();
    }

    private void prepareProcessIteratorWrappers() {
        volumeProcessPoints = weakformQuadratureTask.volumeTasks();
        dirichletProcessPoints = weakformQuadratureTask.dirichletTasks();
        neumannProcessPoints = weakformQuadratureTask.neumannTasks();
        volumeIteratorWrapper = volumeProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(volumeProcessPoints.iterator());
        neumannIteratorWrapper = neumannProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(neumannProcessPoints.iterator());
        dirichletIteratorWrapper = dirichletProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(dirichletProcessPoints.iterator());
    }

    private void prepareSupportDomainSearcherFactoryWithoutInfluenceRadiusFilter() {
        supportDomainSearcherFactory = new SupportDomainSearcherFactory();
        supportDomainSearcherFactory.setAllMFNodes(model.getAllNodes());
        if (null != model.getPolygon()) {
            supportDomainSearcherFactory.setBoundaries(model.getPolygon().getChainsHeads());
        } else {
            supportDomainSearcherFactory.setSegmentsSearcher(null);
        }
    }

    private void prepareProcessNodesDatas() {
        int index = 0;
        influenceRadiusCalculator.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        for (MFNode nd : model.getSpaceNodes()) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(nd, null);
            nd.setInfluenceRadius(rad);
            nd.setAssemblyIndex(index++);
        }

        if (null != model.getPolygon()) {
            for (Segment seg : model.getPolygon()) {
                MFNode nd = (MFNode) seg.getStart();
                influenceRadiusCalculator.calcInflucenceRadius(nd, seg);
                nd.setAssemblyIndex(index++);
            }
        }

        maxInfluenceRadius = MFNode.calcMaxInfluenceRadius(model.getAllNodes());

        extraLagDirichletNodes = new LinkedList<>();
        int nodeId = model.getAllNodes().size();
        if (isAssemblyDirichletByLagrange()) {
            for (WeakformQuadraturePoint qp : dirichletProcessPoints) {
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

    void prepareAssemblier() {
        if (null != constitutiveLaw) {
            assemblier.setConstitutiveLaw(constitutiveLaw);
        }
        assemblier.setNodesNum(model.getAllNodes().size());
        boolean dense = model.getAllNodes().size() <= DENSE_MATRIC_SIZE_THRESHOLD;
        assemblier.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor();
            int dirichletNodesSize = LinearLagrangeDirichletProcessor.calcDirichletNodesSize(model.getAllNodes());
            if (!extraLagDirichletNodes.isEmpty()) {
                dirichletNodesSize += LinearLagrangeDirichletProcessor.calcDirichletNodesSize(extraLagDirichletNodes);
            }
            WeakformLagrangeAssemblier sL = (WeakformLagrangeAssemblier) assemblier;
            sL.setDirichletNodesNum(dirichletNodesSize);
        }
        assemblier.prepare();
        logger.info(
                "prepared assemblier: {}",
                assemblier);
    }

    public boolean isAssemblyDirichletByLagrange() {
        return assemblier instanceof WeakformLagrangeAssemblier;
    }

    public PostProcessor postProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(shapeFunction.synchronizeClone());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        result.setMaxInfluenceRad(maxInfluenceRadius);
        return result;
    }

    public WeakformQuadratureTask getWeakformQuadratureTask() {
        return weakformQuadratureTask;
    }

    public void setWeakformQuadratureTask(WeakformQuadratureTask weakformQuadratureTask) {
        this.weakformQuadratureTask = weakformQuadratureTask;
    }

    public Model2D getModel() {
        return model;
    }

    public void setModel(Model2D model) {
        this.model = model;
    }

    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public WeakformAssemblier getAssemblier() {
        return assemblier;
    }

    public void setAssemblier(WeakformAssemblier assemblier) {
        this.assemblier = assemblier;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public int getNodeValueDimension() {
        return assemblier.getNodeValueDimension();
    }

    @Override
    public WeakformProcessor produce() {
        prepare();
        WeakformProcessor result = new WeakformProcessor();
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

    private WeakformProcessRunnable produceRunnable() {
        WeakformAssemblier produceAssemblier = produceAssemblier();
        Mixer mixer = produceMixer();
        WeakformProcessRunnable runnable = new WeakformProcessRunnable();
        runnable.setAssemblier(produceAssemblier);
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

    private WeakformAssemblier produceAssemblier() {
        return assemblier.synchronizeClone();
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

    public static WeakformProcessorFactory genTimoshenkoProjectProcessFactory() {
        TimoshenkoAnalyticalBeam2D timoBeam =
                new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoStandardTask task =
                new TimoshenkoStandardTask(timoBeam, quadDomainSize, quadDomainSize, quadDegree);
        WeakformProcessorFactory weakformProcessorFactory = new WeakformProcessorFactory();
        weakformProcessorFactory.setup(task.processPackage(quadDomainSize, inflRads));
        return weakformProcessorFactory;
    }

    public static void main(String[] args) {
        WeakformProcessorFactory processFactory = genTimoshenkoProjectProcessFactory();
        processFactory.setEnableMultiThread(false);
        WeakformProcessor process = processFactory.produce();
        process.process();
        process.solve();
        PostProcessor pp = processFactory.postProcessor();
        double[] value = pp.value(new double[]{0.1, 0}, null);
        System.out.println("value = " + Arrays.toString(value));
    }
}
