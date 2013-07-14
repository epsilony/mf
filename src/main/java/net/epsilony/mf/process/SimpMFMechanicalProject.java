/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.Arrays;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import static net.epsilony.mf.process.SimpMfProject.genTimoshenkoProjectProcessFactory;
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

    public static void main(String[] args) {
        SimpMFMechanicalProject project = (SimpMFMechanicalProject) genTimoshenkoProjectProcessFactory().produce();
        project.setEnableMultiThread(false);
        project.process();
        project.solve();

        PostProcessor pp = project.genPostProcessor();
        double[] value = pp.value(new double[]{0.1, 0}, null);
        System.out.println("value = " + Arrays.toString(value));
    }
}
