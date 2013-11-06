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
        double factor = weight * penalty;
        MFMatrix mat = mainMatrix;
        MFMatrix vec = mainVector;
        double[] lvs = testShapeFunctionValues[0];
        double[] rvs = trialShapeFunctionValues[0];

        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * valueDimension;
            double lvi = lvs[i];
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValidity[dim]) {
                    vec.add(row + dim, 0, lvi * load[dim] * factor);
                }
            }
            int jStart = 0;
            if (mainMatrix.isUpperSymmetric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * valueDimension;
                double vij = factor * lvi * rvs[j];
                int tRow;
                int tCol;
                if (mainMatrix.isUpperSymmetric() && col <= row) {
                    tRow = col;
                    tCol = row;
                } else {
                    tRow = row;
                    tCol = col;
                }
                for (int dim = 0; dim < valueDimension; dim++) {
                    if (!loadValidity[dim]) {
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
                + String.format("{nodes*dim: %d*%d, penalty %f}",
                        getNodesNum(),
                        getSpatialDimension(),
                        getPenalty());
    }
}
