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

import net.epsilony.mf.model.load.DirichletLoadValue;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDirichletAssembler extends AbstractAssembler implements LagrangleAssembler {

    protected int lagrangleNodesNum;

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        super.setMainMatrix(mainMatrix);
        prepareMainMatrixLarangleDiagConvention();
    }

    private void prepareMainMatrixLarangleDiagConvention() {
        if (valueDimension < 1) {
            throw new IllegalArgumentException();
        }
        final int mainMatrixSize = mainMatrix.numRows();
        for (int i = mainMatrixSize - lagrangleNodesNum * valueDimension; i < mainMatrixSize; i++) {
            mainMatrix.set(i, i, 1);
        }
    }

    @Override
    public void assemble() {
        double weight = assemblyInput.getWeight();
        LagrangleAssemblyInput lagAssemblyInput = (LagrangleAssemblyInput) assemblyInput;
        DirichletLoadValue loadValue = (DirichletLoadValue) lagAssemblyInput.getLoadValue();
        ShapeFunctionValue testLagrangleValue = lagAssemblyInput.getLagrangleT2Value().getTestValue();
        T2Value t2Value = lagAssemblyInput.getT2Value();
        ShapeFunctionValue trialValue = t2Value.getTrialValue();
        for (int i = 0; i < testLagrangleValue.size(); i++) {
            int testLagIndex = testLagrangleValue.getNodeAssemblyIndex(i);
            double testLagValue = testLagrangleValue.get(i, 0);
            double vecValue = testLagValue * weight;
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValue.validity(dim)) {
                    mainVector.add(testLagIndex * valueDimension + dim, 0, vecValue * loadValue.value(dim));
                }
            }
            for (int j = 0; j < trialValue.size(); j++) {
                int trialIndex = trialValue.getNodeAssemblyIndex(j);
                double trialV = trialValue.get(j, 0);

                double matValueLeftDown = vecValue * trialV;

                for (int dim = 0; dim < valueDimension; dim++) {
                    int rowDownLeft = testLagIndex * valueDimension + dim;
                    int colDownLeft = trialIndex * valueDimension + dim;
                    if (loadValue.validity(dim)) {
                        mainMatrix.add(rowDownLeft, colDownLeft, matValueLeftDown);
                        mainMatrix.set(rowDownLeft, rowDownLeft, 0);
                    }
                }
            }
        }

        ShapeFunctionValue testValue = t2Value.getTestValue();
        ShapeFunctionValue trialLagValue = lagAssemblyInput.getLagrangleT2Value().getTrialValue();

        for (int i = 0; i < testValue.size(); i++) {
            int testIndex = testValue.getNodeAssemblyIndex(i);
            double testV = testValue.get(i, 0);
            double vecValue = testV * weight;

            for (int j = 0; j < trialLagValue.size(); j++) {
                int trialLagIndex = trialLagValue.getNodeAssemblyIndex(j);
                double trialLagV = trialLagValue.get(j, 0);

                double matValueUpRight = vecValue * trialLagV;

                for (int dim = 0; dim < valueDimension; dim++) {
                    int rowUpRight = testIndex * valueDimension + dim;
                    int colUpRight = trialLagIndex * valueDimension + dim;
                    if (loadValue.validity(dim)) {
                        mainMatrix.add(rowUpRight, colUpRight, matValueUpRight);
                    }
                }
            }
        }
    }

    @Override
    public void setLagrangleNodesNum(int allLagrangleNodesNum) {
        this.lagrangleNodesNum = allLagrangleNodesNum;
    }
}
