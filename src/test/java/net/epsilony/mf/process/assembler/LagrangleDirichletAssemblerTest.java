/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import java.util.Random;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDirichletAssemblerTest extends AssemblerTestTemplate<LagrangleDirichletAssembler, LagrangleDirichletAssemblerTest.LagrangleAssemblerTestData> {

    public static final String PYTHON_SCRIPT = "lagrangle_dirichlet_assembler.py";
    long randomSeed = 147;
    Random rand = new Random(147);

    public LagrangleDirichletAssemblerTest() {
        super(LagrangleDirichletAssembler.class, LagrangleAssemblerTestData.class);
    }

    @Override
    protected String getPythonScriptName() {
        return PYTHON_SCRIPT;
    }

    @Override
    protected Random getRandom() {
        return rand;
    }

    public static class LagrangleAssemblerTestData extends AssemblerTestData {

        int allLagrangleNodesSize;
        double[][] lagrangleShapeFunction;
        int[] lagrangleAssemblyIndes;
    }

    @Override
    protected int getMainMatrixSize(LagrangleAssemblerTestData data) {
        return (data.allLagrangleNodesSize + data.allNodesSize) * data.valueDimension;
    }

    @Override
    protected void initAssembler(LagrangleAssemblerTestData data) {
        super.initAssembler(data);
        assembler.setAllLagrangleNodesNum(data.allLagrangleNodesSize);
    }

    @Override
    protected void setAssembler(LagrangleAssemblerTestData data) {
        super.setAssembler(data);
        assembler.setLagrangeShapeFunctionValue(new TIntArrayList(data.lagrangleAssemblyIndes), data.lagrangleShapeFunction[0]);
    }
}
