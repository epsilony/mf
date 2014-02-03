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

import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDirichletAssembler extends AbstractAssembler implements LagrangleAssembler {

    protected int allLagrangleNodesNum;
    protected ShapeFunctionValue lagrangleValue;

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        super.setMainMatrix(mainMatrix);
        prepareMainMatrixLarangleDiagConvention();
    }

    private void prepareMainMatrixLarangleDiagConvention() {
        final int mainMatrixSize = mainMatrix.numRows();
        for (int i = mainMatrixSize - allLagrangleNodesNum * valueDimension; i < mainMatrixSize; i++) {
            mainMatrix.set(i, i, 1);
        }
    }

    @Override
    public int getRequiredMatrixSize() {
        return valueDimension * (allNodesNum + allLagrangleNodesNum);
    }

    public ShapeFunctionValue getLagrangleValue() {
        return lagrangleValue;
    }

    @Override
    public void setLagrangleInput(ShapeFunctionValue lagrangleValue) {
        this.lagrangleValue = lagrangleValue;
    }

    @Override
    public void assemble() {
        double weight = assemblyInput.getWeight();
        LoadValue loadValue = assemblyInput.getLoadValue();
        TTValue ttValue = assemblyInput.getTTValue();

        for (int i = 0; i < lagrangleValue.getNodesSize(); i++) {
            int lagIndex = lagrangleValue.getNodeAssemblyIndex(i);
            double lagShapeFunc = lagrangleValue.getValue(i, 0);
            double vecValue = lagShapeFunc * weight;
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValue.validity(dim)) {
                    mainVector.add(lagIndex * valueDimension + dim, 0, vecValue * loadValue.value(dim));
                }
            }
            for (int j = 0; j < ttValue.getNodesSize(); j++) {
                int ttIndex = ttValue.getNodeAssemblyIndex(j);
                double testValue = ttValue.getTestValue(j, 0);
                double trialValue = ttValue.getTrialValue(j, 0);

                double matValueDownLeft = lagShapeFunc * trialValue * weight;
                double matValueUpRight = lagShapeFunc * testValue * weight;

                for (int dim = 0; dim < valueDimension; dim++) {
                    int rowDownLeft = lagIndex * valueDimension + dim;
                    int colDownLeft = ttIndex * valueDimension + dim;
                    if (loadValue.validity(dim)) {
                        mainMatrix.add(rowDownLeft, colDownLeft, matValueDownLeft);
                        mainMatrix.add(colDownLeft, rowDownLeft, matValueUpRight);
                        mainMatrix.set(rowDownLeft, rowDownLeft, 0);
                    }
                }
            }
        }
    }

    @Override
    public void setAllLagrangleNodesNum(int allLagrangleNodesNum) {
        this.allLagrangleNodesNum = allLagrangleNodesNum;
    }

    @Override
    public int getLagrangleDimension() {
        return allLagrangleNodesNum * valueDimension;
    }
}
