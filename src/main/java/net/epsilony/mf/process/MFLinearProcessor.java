/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.Map;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.RawMFIntegrateTask;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.util.MFConstants;
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
    protected RawMFIntegrateTask integrateTask = new RawMFIntegrateTask();
    protected Map<String, Object> settings = MFProcessorSettings.defaultSettings();
    protected MFIntegrateProcessor integrateProcess = new MFIntegrateProcessor();

    public void setProject(MFProject linearProject) {
        this.project = linearProject;
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
        solver.setNodes(nodesIndesProcessor.getAllProcessNodes());
        solver.solve();
    }

    public PostProcessor genPostProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(SerializationUtils.clone(project.getShapeFunction()));
        result.setNodeValueDimension(project.getAssembler().getDimension());
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
        integrateProcess.setIntegrateTask(integrateTask);
        integrateProcess.setMixerFactory(mixerFactory);
        integrateProcess.setEnableMultiThread(isEnableMultiThread());
        integrateProcess.process();
    }

    private void prepareIntegrateTask() {
        MFIntegrateTask projectTask = project.getMFIntegrateTask();
        integrateTask.setVolumeTasks(projectTask.volumeTasks());
        integrateTask.setNeumannTasks(projectTask.neumannTasks());
        integrateTask.setDirichletTasks(projectTask.dirichletTasks());
        logger.info("made a integrate task buffer {}", integrateTask);
    }

    private void prepareProcessNodesDatas() {
        GeomModel2D model = project.getModel();

        nodesIndesProcessor.setSpaceNodes(model.getSpaceNodes());
        nodesIndesProcessor.setBoundaries(model.getPolygon().getSegments());
        nodesIndesProcessor.setApplyDirichletByLagrange(isAssemblyDirichletByLagrange());
        nodesIndesProcessor.setDirichletTasks(project.getMFIntegrateTask().dirichletTasks());
        nodesIndesProcessor.process();

        nodesInfluenceRadiusProcessor.setAllNodes(nodesIndesProcessor.getAllGeomNodes());
        nodesInfluenceRadiusProcessor.setSpaceNodes(model.getSpaceNodes());
        nodesInfluenceRadiusProcessor.setBoundaries(model.getPolygon().getSegments());
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
}
