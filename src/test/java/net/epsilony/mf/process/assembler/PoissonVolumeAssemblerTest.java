/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PoissonVolumeAssemblerTest extends AssemblerTestTemplate<PoissonVolumeAssembler, AssemblerTestData> {

    private final String python_script = "poisson_volume_assembler.py";

    public PoissonVolumeAssemblerTest() {
        super(PoissonVolumeAssembler.class, AssemblerTestData.class);
    }

    @Override
    protected String getPythonScriptName() {
        return python_script;
    }
}
