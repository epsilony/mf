/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.cons_law.RawConstitutiveLaw;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalVolumeAssemblerTest extends AssemblerTestTemplate<MechanicalVolumeAssembler, MechanicalVolumeAssemblerTest.MechanicalVolumeTestData> {

    private final String python_script = "mechanical_volume_assembler.py";

    @Override
    protected String getPythonScriptName() {
        return python_script;
    }

    public static class MechanicalVolumeTestData extends AssemblerTestData {

        public double[][] constitutiveLaw;

    }

    public MechanicalVolumeAssemblerTest() {
        super(MechanicalVolumeAssembler.class, MechanicalVolumeTestData.class);
    }

    @Override
    protected void initAssembler(MechanicalVolumeTestData data) {
        super.initAssembler(data);
        assembler.setConstitutiveLaw(new RawConstitutiveLaw(new DenseMatrix64F(data.constitutiveLaw)));
    }

}
