/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import java.util.Random;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDirichletAssemblerTest extends
        AssemblerTestTemplate<LagrangleDirichletAssembler, LagrangleDirichletAssemblerTest.LagrangleAssemblerTestData> {

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
        assembler.setLagrangeShapeFunctionValue(new TIntArrayList(data.lagrangleAssemblyIndes),
                data.lagrangleShapeFunction[0]);
    }
}
