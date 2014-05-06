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
package net.epsilony.mf.implicit.assembler;

import net.epsilony.mf.process.assembler.AbstractAssembler;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.shape_func.ShapeFunctionValue;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ApproximateVolumeAssembler extends AbstractAssembler {

    @Override
    public void assemble() {
        double weight = assemblyInput.getWeight();
        T2Value t2Value = assemblyInput.getT2Value();
        ShapeFunctionValue testValue = t2Value.getTestValue();
        ShapeFunctionValue trialValue = t2Value.getTrialValue();
        for (int ri = 0; ri < testValue.size(); ri++) {
            int row = testValue.getNodeAssemblyIndex(ri);
            double wRowValue = weight * testValue.get(ri, 0);
            for (int cj = 0; cj < testValue.size(); cj++) {
                int col = trialValue.getNodeAssemblyIndex(cj);
                double colValue = trialValue.get(cj, 0);
                mainMatrix.add(row, col, colValue * wRowValue);
            }
        }

    }

}
