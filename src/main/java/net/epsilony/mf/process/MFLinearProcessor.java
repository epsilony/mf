/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.GeomModel2DUtils;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.RawMFIntegrateTask;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.util.MFConstants;
import net.epsilony.tb.solid.GeomUnit;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLinearProcessor {

    public static Logger logger = LoggerFactory.getLogger(MFLinearProcessor.class);
    protected MFProject project;
    protected MFNodesIndesProcessor nodesIndesProcessor = new MFNodesIndesProcessor();
    protected MFNodesInfluenceRadiusProcessor nodesInfluenceRadiusProcessor = new MFNodesInfluenceRadiusProcessor();
    protected MFMixerFactory mixerFactory = new MFMixerFactory();
    protected Assembler assembler;
    protected LinearLagrangeDirichletProcessor lagProcessor;
    protected RawMFIntegrateTask integrateTaskCopy = new RawMFIntegrateTask();
    protected Map<String, Object> settings = MFProcessorSettings.defaultSettings();
    protected MFIntegrateProcessor integrateProcess = new MFIntegrateProcessor();

    public void setProject(MFProject project) {
        this.project = project;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void preprocess() {
        logger.info("start preprocessing");
        prepare();
        integrate();
    }

    public IntegrateResult getIntegrateResult() {
        return integrateProcess.getIntegrateResult();
    }

    public void solve() {
        MFSolver solver = project.getMFSolver();
        IntegrateResult integrateResult = getIntegrateResult();
        solver.setMainMatrix(integrateResult.getMainMatrix());
        solver.setMainVector(integrateResult.getMainVector());
        solver.setUpperSymmetric(integrateResult.isUpperSymmetric());
        solver.solve();

        fillNodeValues(solver.getResult());
    }

    private void fillNodeValues(DenseVector result) {
        int nodeValueDimension = assembler.getValueDimension();
        for (MFNode node : nodesIndesProcessor.getAllProcessNodes()) {
            int nodeValueIndex = node.getAssemblyIndex() * nodeValueDimension;
            if (nodeValueIndex >= 0) {
                double[] nodeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    nodeValue[i] = result.get(i + nodeValueIndex);
                    node.setValue(nodeValue);
                }
            }
            int lagrangeValueIndex = node.getLagrangeAssemblyIndex();
            Matrix mainMatrix = integrateProcess.getIntegrateResult().getMainMatrix();
            if (lagrangeValueIndex >= 0) {
                double[] lagrangeValue = new double[nodeValueDimension];
                boolean[] lagrangeValueValidity = new boolean[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    int index = lagrangeValueIndex * nodeValueDimension + i;
                    lagrangeValue[i] = result.get(index);
                    lagrangeValueValidity[i] = mainMatrix.get(index, index) == 0;  //a prototyle of validity
                }
                node.setLagrangeValue(lagrangeValue);
                node.setLagrangeValueValidity(lagrangeValueValidity);
            }
        }
        logger.info("filled nodes values");
    }

    public PostProcessor genPostProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(SerializationUtils.clone(project.getShapeFunction()));
        result.setNodeValueDimension(project.getAssembler().getValueDimension());
        result.setSupportDomainSearcher(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory().produce());
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        return result;
    }

    private void prepare() {
        logger.info("start preparing");
        prepareIntegrateTask();
        prepareProcessNodesDatas();
        prepareMixerFactory();
        prepareAssembler();
        logger.info("prepared!");
    }

    private void integrate() {
        logger.info("start integrating");

        logger.info("integrate processor: {}", integrateProcess);
        integrateProcess.setAssembler(assembler);
        integrateProcess.setIntegrateTask(integrateTaskCopy);
        integrateProcess.setMixerFactory(mixerFactory);
        integrateProcess.setEnableMultiThread(isEnableMultiThread());
        integrateProcess.setForcibleThreadNum(getForcibleThreadNum());
        integrateProcess.process();
    }

    private void prepareIntegrateTask() {
        MFIntegrateTask projectTask = project.getMFIntegrateTask();
        integrateTaskCopy.setVolumeTasks(projectTask.volumeTasks());
        integrateTaskCopy.setNeumannTasks(projectTask.neumannTasks());
        integrateTaskCopy.setDirichletTasks(projectTask.dirichletTasks());
        logger.info("made a integrate task buffer {}", integrateTaskCopy);
    }

    private void prepareProcessNodesDatas() {
        AnalysisModel model = project.getModel();

        nodesIndesProcessor.setSpaceNodes(model.getSpaceNodes());
        nodesIndesProcessor.setGeomRoot(model.getFractionizedModel().getGeomRoot());
        nodesIndesProcessor.setApplyDirichletByLagrange(isAssemblyDirichletByLagrange());
        nodesIndesProcessor.setDirichletBnds(searchDirichletBnds(model));
        nodesIndesProcessor.setDimension(project.getShapeFunction().getDimension());
        nodesIndesProcessor.process();

        nodesInfluenceRadiusProcessor.setAllNodes(nodesIndesProcessor.getAllGeomNodes());
        nodesInfluenceRadiusProcessor.setSpaceNodes(nodesIndesProcessor.getSpaceNodes());
        nodesInfluenceRadiusProcessor.setDimension(project.getShapeFunction().getDimension());
        switch (project.getShapeFunction().getDimension()) {
            case 1:
                nodesInfluenceRadiusProcessor.setBoundaries(null);
                break;
            case 2:
                nodesInfluenceRadiusProcessor.setBoundaries(GeomModel2DUtils.getAllSegments(project.getModel().getFractionizedModel().getGeomRoot()));
                break;
            default:
                throw new IllegalStateException();
        }
        nodesInfluenceRadiusProcessor.setInfluenceRadiusCalculator(project.getInfluenceRadiusCalculator());
        nodesInfluenceRadiusProcessor.process();

        logger.info("nodes datas prepared");
    }

    private void prepareMixerFactory() {
        logger.info("start preparing mixer factory");
        logger.info("shape function: {}", project.getShapeFunction());
        mixerFactory.setMaxNodesInfluenceRadius(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        mixerFactory.setShapeFunction(project.getShapeFunction());
        mixerFactory.setSupportDomainSearcherFactory(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory());

    }

    protected void prepareAssembler() {
        logger.info("start preparing assembler");
        assembler = project.getAssembler();
        int allGeomNodesSize = nodesIndesProcessor.getAllGeomNodes().size();
        assembler.setNodesNum(allGeomNodesSize);
        boolean dense = nodesIndesProcessor.getAllProcessNodes().size() <= MFConstants.DENSE_MATRIC_SIZE_THRESHOLD;
        assembler.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor();
            int dirichletNodesSize = LinearLagrangeDirichletProcessor.calcLagrangeNodesNum(nodesIndesProcessor.getAllProcessNodes());
            LagrangeAssembler sL = (LagrangeAssembler) assembler;
            sL.setLagrangeNodesSize(dirichletNodesSize);
        }
        logger.info(
                "prepared assembler: {}",
                assembler);
    }

    protected boolean isAssemblyDirichletByLagrange() {
        return project.getAssembler() instanceof LagrangeAssembler;
    }

    private boolean isEnableMultiThread() {
        return (boolean) settings.get(MFConstants.KEY_ENABLE_MULTI_THREAD);
    }

    private Integer getForcibleThreadNum() {
        return (Integer) settings.get(MFConstants.KEY_FORCIBLE_THREAD_NUMBER);
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

    public static List<GeomUnit> searchDirichletBnds(AnalysisModel model) {
        LinkedList<GeomUnit> dirichletBnd = new LinkedList<>();

        for (Map.Entry<GeomUnit, MFLoad> entry : model.getFractionizedModel().getLoadMap().entrySet()) {
            MFLoad load = entry.getValue();
            if (load instanceof SegmentLoad) {
                SegmentLoad segLoad = (SegmentLoad) load;
                if (!segLoad.isDirichlet()) {
                    continue;
                }
            } else if (load instanceof NodeLoad) {
                NodeLoad nodeLoad = (NodeLoad) load;
                if (!nodeLoad.isDirichlet()) {
                    continue;
                }
            } else {
                continue;
            }


            dirichletBnd.add(entry.getKey());
        }
        return dirichletBnd;
    }
}
