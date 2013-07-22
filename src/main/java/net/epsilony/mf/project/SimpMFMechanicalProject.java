/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.project.sample.TimoshenkoBeamProjectFactory;
import java.util.Arrays;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import static net.epsilony.mf.project.SimpMfProject.genTimoshenkoProjectProcessFactory;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.MechanicalAssembler;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFMechanicalProject extends SimpMfProject implements MFMechanicalProject {

    ConstitutiveLaw constitutiveLaw;

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw cLaw) {
        constitutiveLaw = cLaw;
        MechanicalAssembler mAsm = (MechanicalAssembler) assembler;
        mAsm.setConstitutiveLaw(constitutiveLaw);
    }

    @Override
    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    @Override
    public MechanicalAssembler<?> getAssembler() {
        return (MechanicalAssembler<?>) super.getAssembler();
    }

    @Override
    public void setAssembler(Assembler<?> assembler) {
        if (!(assembler instanceof MechanicalAssembler)) {
            throw new IllegalArgumentException("assemblier must implements MechanicalAssemblier");
        }
        super.setAssembler(assembler);
    }

    public MechanicalPostProcessor genMechanicalPostProcessor() {
        MechanicalPostProcessor result = new MechanicalPostProcessor();
        result.setConstitutiveLaw(constitutiveLaw);
        result.setMaxInfluenceRad(model.getMaxInfluenceRadius());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setShapeFunction(shapeFunction);
        result.setSupportDomainSearcher(model.getSupportDomainSearcherFactory().produce());
        return result;
    }

    public static void main(String[] args) {
        TimoshenkoBeamProjectFactory timo = genTimoshenkoProjectProcessFactory();
        SimpMFMechanicalProject project = (SimpMFMechanicalProject) timo.produce();
        project.setEnableMultiThread(false);
        project.process();
        project.solve();

        PostProcessor pp = project.genPostProcessor();
        MechanicalPostProcessor mpp = project.genMechanicalPostProcessor();
        double[] engineeringStrain = mpp.engineeringStrain(new double[]{1, 0}, null);
        System.out.println("engineeringStrain = " + Arrays.toString(engineeringStrain));
        double[] expStrain = timo.getTimoBeam().strain(1, 0, null);
        System.out.println("expStraint = " + Arrays.toString(expStrain));
        double[] value = pp.value(new double[]{1, 0}, null);
        System.out.println("value = " + Arrays.toString(value));
    }
}
