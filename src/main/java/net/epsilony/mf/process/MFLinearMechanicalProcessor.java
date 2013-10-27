/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.Map;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.MechanicalVolumeAssembler;
import net.epsilony.mf.project.MFProject;
import static net.epsilony.mf.project.MFProjectKey.*;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLinearMechanicalProcessor extends MFLinearProcessor {

    @Override
    public void setProject(MFProject project) {
        if (project.get(CONSTITUTIVE_LAW) == null) {
            throw new IllegalArgumentException();
        }
        super.setProject(project);
    }

    @Override
    protected void prepareAssemblersGroup() {
        super.prepareAssemblersGroup();
        Map<MFProcessType, Assembler> assemblerGroup = (Map<MFProcessType, Assembler>) project.get(ASSEMBLERS_GROUP);
        MechanicalVolumeAssembler meVolAssembler = (MechanicalVolumeAssembler) assemblerGroup.get(MFProcessType.VOLUME);
        meVolAssembler.setConstitutiveLaw((ConstitutiveLaw) project.get(CONSTITUTIVE_LAW));
    }

    public MechanicalPostProcessor genMechanicalPostProcessor() {
        MechanicalPostProcessor result = new MechanicalPostProcessor();

        result.setConstitutiveLaw((ConstitutiveLaw) project.get(CONSTITUTIVE_LAW));
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        result.setNodeValueDimension((int) project.get(VALUE_DIMENSION));
        result.setShapeFunction((MFShapeFunction) project.get(SHAPE_FUNCTION));
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
