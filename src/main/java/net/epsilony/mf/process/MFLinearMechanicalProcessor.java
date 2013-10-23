/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.process.assembler.MechanicalVolumeAssembler;
import net.epsilony.mf.project.MFMechanicalProject;
import net.epsilony.mf.project.MFProject;

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
    protected void prepareAssemblersGroup() {
        super.prepareAssemblersGroup();
        MechanicalVolumeAssembler meVolAssembler = (MechanicalVolumeAssembler) project.getAssemblersGroup().get(MFProcessType.VOLUME);
        meVolAssembler.setConstitutiveLaw(((MFMechanicalProject) project).getConstitutiveLaw());
    }

    public MechanicalPostProcessor genMechanicalPostProcessor() {
        MechanicalPostProcessor result = new MechanicalPostProcessor();
        MFMechanicalProject mechanicalProject = (MFMechanicalProject) project;
        result.setConstitutiveLaw(mechanicalProject.getConstitutiveLaw());
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        result.setNodeValueDimension(project.getValueDimension());
        result.setShapeFunction(project.getShapeFunction());
        result.setSupportDomainSearcher(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory().produce());
        return result;
    }

    public static void main(String[] args) {
//        TimoshenkoBeamProjectFactory timo = genTimoshenkoProjectFactory();
//        SimpMFMechanicalProject project = (SimpMFMechanicalProject) timo.produce();
//        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
//        processor.setProject(project);
//        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
//        processor.preprocess();
//        processor.solve();
//
//        PostProcessor pp = processor.genPostProcessor();
//        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
//        double[] engineeringStrain = mpp.engineeringStrain(new double[]{1, 0}, null);
//        System.out.println("engineeringStrain = " + Arrays.toString(engineeringStrain));
//        double[] expStrain = timo.getTimoBeam().strain(1, 0, null);
//        System.out.println("expStraint = " + Arrays.toString(expStrain));
//        double[] value = pp.value(new double[]{1, 0}, null);
//        System.out.println("value = " + Arrays.toString(value));
    }
}
