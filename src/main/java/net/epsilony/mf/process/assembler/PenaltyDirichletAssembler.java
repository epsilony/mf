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
import net.epsilony.tb.MiscellaneousUtils;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PenaltyDirichletAssembler extends AbstractAssembler {

    double penalty = 1e-6;

    public PenaltyDirichletAssembler() {
    }

    public PenaltyDirichletAssembler(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    @Override
    public void assemble() {
        double weight = assemblyInput.getWeight();
        DirichletLoadValue loadValue = (DirichletLoadValue) assemblyInput.getLoadValue();

        double factor = weight * penalty;
        MFMatrix mat = mainMatrix;
        MFMatrix vec = mainVector;
        ShapeFunctionValue testValue = assemblyInput.getT2Value().getTestValue();
        ShapeFunctionValue trialValue = assemblyInput.getT2Value().getTrialValue();
        for (int i = 0; i < testValue.size(); i++) {
            int row = testValue.getNodeAssemblyIndex(i) * valueDimension;
            double testV = testValue.get(i, 0);
            final double factoredTestV = testV * factor;
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValue.validity(dim)) {
                    vec.add(row + dim, 0, factoredTestV * loadValue.value(dim));
                }
            }
            int jStart = 0;
            for (int j = jStart; j < trialValue.size(); j++) {
                int col = trialValue.getNodeAssemblyIndex(j) * valueDimension;
                double vij = factoredTestV * trialValue.get(j, 0);
                int tRow;
                int tCol;
                tRow = row;
                tCol = col;
                for (int dim = 0; dim < valueDimension; dim++) {
                    if (!loadValue.validity(dim)) {
                        continue;
                    }
                    mat.add(tRow + dim, tCol + dim, vij);
                }
            }
        }
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*dim: %d*%d, penalty %f}", getAllNodesNum(), getSpatialDimension(), getPenalty());
    }
}
