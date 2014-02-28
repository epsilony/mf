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
public class VirtualLoadWorkAssembler extends AbstractAssembler<AssemblyInput<? extends LoadValue>> {

    @Override
    public void assemble() {
        MFMatrix vec = mainVector;
        double weight = assemblyInput.getWeight();
        LoadValue loadValue = assemblyInput.getLoadValue();
        ShapeFunctionValue testValue = assemblyInput.getTestValue();
        for (int i = 0; i < testValue.getNodesSize(); i++) {
            int vecIndex = testValue.getNodeAssemblyIndex(i) * valueDimension;
            double v = testValue.getValue(i, 0);
            for (int valueDim = 0; valueDim < valueDimension; valueDim++) {
                vec.add(vecIndex + valueDim, 0, v * loadValue.value(valueDim) * weight);
            }
        }
    }

}
