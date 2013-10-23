/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PenaltyDirichletAssemblerTest extends AssemblerTestTemplate<PenaltyDirichletAssembler, PenaltyDirichletAssemblerTest.PenalityTestData> {

    private final String python_script = "penality_dirichlet_assembler.py";

    public static class PenalityTestData extends AssemblerTestData {

        double penalty;
    }

    public PenaltyDirichletAssemblerTest() {
        super(PenaltyDirichletAssembler.class, PenalityTestData.class);
    }

    @Override
    protected String getPythonScriptName() {
        return python_script;
    }

    @Override
    protected void initAssembler(PenalityTestData data) {
        super.initAssembler(data);
        assembler.setPenalty(data.penalty);
    }

}
