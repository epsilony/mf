/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import java.util.Random;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class NeumannAssemblerTest extends AssemblerTestTemplate<NeumannAssembler, AssemblerTestData> {

    private final String python_script = "neumann_assembler.py";

    public NeumannAssemblerTest() {
        super(NeumannAssembler.class, AssemblerTestData.class);
    }

    @Override
    protected String getPythonScriptName() {
        return python_script;
    }
}
