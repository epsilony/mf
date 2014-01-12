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

package net.epsilony.mf.implicit;

import net.epsilony.mf.process.assembler.AbstractAssembler;
import net.epsilony.tb.common_func.RadialBasisCore;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LevelSetApproximationAssembler extends AbstractAssembler {

    RadialBasisCore weightFunction;

    public RadialBasisCore getWeightFunction() {
        return weightFunction;
    }

    public void setWeightFunction(RadialBasisCore weightFunction) {
        this.weightFunction = weightFunction;
        weightFunction.setDiffOrder(0);
    }

    private final double[] weightFunctionValue = new double[1];

    @Override
    public void assemble() {
        double aimFunc = load[0];
        double wholeWeight = weight * weightFunction.valuesByDistance(aimFunc, weightFunctionValue)[0];
        double[] rShapeFunc = trialShapeFunctionValues[0];
        double[] lShapeFunc = testShapeFunctionValues[0];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i);
            double rowShapeFunc = lShapeFunc[i];
            mainVector.add(row, 0, wholeWeight * aimFunc * rowShapeFunc);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j);
                double colShapeFunc = rShapeFunc[j];
                mainMatrix.add(row, col, wholeWeight * rowShapeFunc * colShapeFunc);
            }
        }
    }
}
