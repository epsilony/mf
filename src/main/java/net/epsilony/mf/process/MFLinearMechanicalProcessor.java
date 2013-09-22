/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.Arrays;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.assembler.MechanicalAssembler;
import net.epsilony.mf.project.MFMechanicalProject;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import static net.epsilony.mf.project.SimpMFMechanicalProject.genTimoshenkoProjectFactory;
import net.epsilony.mf.project.sample.TimoshenkoBeamProjectFactory;
import net.epsilony.mf.util.MFConstants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLinearMechanicalProcessor extends MFLinearProcessor {

    @Override
    public void setProject(MFProject linearProject) {
        if (!(linearProject instanceof MFMechanicalProject)) {
            throw new IllegalArgumentException();
        }
        super.setProject(linearProject);
    }

    public void setProject(MFMechanicalProject linearProject) {
        this.project = linearProject;
    }

    @Override
    protected void prepareAssembler() {
        logger.info("start preparing assembler");
        MFMechanicalProject mechanicalProject = (MFMechanicalProject) project;
        assembler = mechanicalProject.getAssembler();
        this.assembler.setNodesNum(nodesIndesProcessor.getAllGeomNodes().size());
        boolean dense = nodesIndesProcessor.getAllProcessNodes().size() <= MFConstants.DENSE_MATRIC_SIZE_THRESHOLD;
        this.assembler.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor();
            int dirichletNodesSize = LinearLagrangeDirichletProcessor.calcLagrangeNodesNum(nodesIndesProcessor.getAllProcessNodes());
            LagrangeAssembler sL = (LagrangeAssembler) this.assembler;
            sL.setLagrangeNodesSize(dirichletNodesSize);
        }
        MechanicalAssembler mAsm = (MechanicalAssembler) assembler;
        mAsm.setConstitutiveLaw(mechanicalProject.getConstitutiveLaw());
        logger.info(
                "prepared assembler: {}",
                this.assembler);

    }

    public MechanicalPostProcessor genMechanicalPostProcessor() {
        MechanicalPostProcessor result = new MechanicalPostProcessor();
        MFMechanicalProject mechanicalProject = (MFMechanicalProject) project;
        result.setConstitutiveLaw(mechanicalProject.getConstitutiveLaw());
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        result.setNodeValueDimension(project.getAssembler().getDimension());
        result.setShapeFunction(project.getShapeFunction());
        result.setSupportDomainSearcher(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory().produce());
        return result;
    }

    public static void main(String[] args) {
        TimoshenkoBeamProjectFactory timo = genTimoshenkoProjectFactory();
        SimpMFMechanicalProject project = (SimpMFMechanicalProject) timo.produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        processor.preprocess();
        processor.solve();

        PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        double[] engineeringStrain = mpp.engineeringStrain(new double[]{1, 0}, null);
        System.out.println("engineeringStrain = " + Arrays.toString(engineeringStrain));
        double[] expStrain = timo.getTimoBeam().strain(1, 0, null);
        System.out.println("expStraint = " + Arrays.toString(expStrain));
        double[] value = pp.value(new double[]{1, 0}, null);
        System.out.println("value = " + Arrays.toString(value));
    }
}
