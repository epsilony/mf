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
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDirichletAssembler extends AbstractAssembler implements LagrangleAssembler {

    protected int allLagrangleNodesNum;
    protected TIntArrayList lagrangleAssemblyIndes;
    protected double[] lagrangleShapeFunctionValue;

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

    @Override
    public void setLagrangleShapeFunctionValue(TIntArrayList lagrangleAssemblyIndes,
            double[] lagrangleShapeFunctionValue) {
        this.lagrangleAssemblyIndes = lagrangleAssemblyIndes;
        this.lagrangleShapeFunctionValue = lagrangleShapeFunctionValue;
    }

    @Override
    public void assemble() {

        for (int i = 0; i < lagrangleAssemblyIndes.size(); i++) {
            int lagIndex = lagrangleAssemblyIndes.getQuick(i);
            double lagShapeFunc = lagrangleShapeFunctionValue[i];
            double vecValue = lagShapeFunc * weight;
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValidity[dim]) {
                    mainVector.add(lagIndex * valueDimension + dim, 0, vecValue * load[dim]);
                }
            }
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int testIndex = nodesAssemblyIndes.getQuick(j);
                double testValue = testShapeFunctionValues[0][j];

                int trialIndex = nodesAssemblyIndes.getQuick(j);
                double trialValue = trialShapeFunctionValues[0][j];

                double matValueDownLeft = lagShapeFunc * trialValue * weight;
                double matValueUpRight = lagShapeFunc * testValue * weight;

                for (int dim = 0; dim < valueDimension; dim++) {
                    int rowDownLeft = lagIndex * valueDimension + dim;
                    int colDownLeft = trialIndex * valueDimension + dim;
                    int rowUpRight = testIndex * valueDimension + dim;
                    int colUpRight = rowDownLeft;
                    if (loadValidity[dim]) {
                        mainMatrix.add(rowDownLeft, colDownLeft, matValueDownLeft);
                        mainMatrix.add(rowUpRight, colUpRight, matValueUpRight);
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
