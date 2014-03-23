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

import java.util.Random;

import net.epsilony.mf.process.assembler.AssemblerTestUtils.AssemblerTestData;
import net.epsilony.mf.process.assembler.AssemblerTestUtils.AssemblyInputAdapter;

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
        assembler.setLagrangleNodesNum(data.allLagrangleNodesSize);
    }

    @Override
    protected void setAssembler(LagrangleAssemblerTestData data) {
        super.setAssembler(data);
        assembler.setAssemblyInput(new LagrangleAssemblyInputAdapter(data));
    }

    static class LagrangleAssemblyInputAdapter extends AssemblyInputAdapter implements LagrangleAssemblyInput {

        public LagrangleAssemblyInputAdapter(LagrangleAssemblerTestData data) {
            super(data);
        }

        @Override
        public T2Value getLagrangleT2Value() {
            return new T2Value() {

                @Override
                public ShapeFunctionValue getTestValue() {
                    return new ShapeFunctionAdapter((LagrangleAssemblerTestData) data);
                }

                @Override
                public ShapeFunctionValue getTrialValue() {
                    return new ShapeFunctionAdapter((LagrangleAssemblerTestData) data);
                }
            };
        }

    }

    static class ShapeFunctionAdapter implements ShapeFunctionValue {
        LagrangleAssemblerTestData data;

        public ShapeFunctionAdapter(LagrangleAssemblerTestData data) {
            this.data = data;
        }

        @Override
        public double getValue(int nd, int pd) {
            return data.lagrangleShapeFunction[pd][nd];
        }

        @Override
        public int getNodeAssemblyIndex(int nd) {
            return data.lagrangleAssemblyIndes[nd];
        }

        @Override
        public int getNodesSize() {
            return data.lagrangleAssemblyIndes.length;
        }

        @Override
        public int getMaxPdOrder() {
            return 0;
        }

        @Override
        public int getSpatialDimension() {
            return data.spatialDimension;
        }

    }
}
