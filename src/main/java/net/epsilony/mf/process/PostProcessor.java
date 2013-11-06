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

package net.epsilony.mf.process;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.WithDiffOrderUtil;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PostProcessor extends Mixer {

    private static final int VARIABLE_DIMENSION = 2;
    int nodeValueDimension;

    public int getNodeValueDimension() {
        return nodeValueDimension;
    }

    public void setNodeValueDimension(int nodeValueDimension) {
        this.nodeValueDimension = nodeValueDimension;
    }

    public double[] value(double[] center, GeomUnit bnd) {
        setCenter(center);
        setBoundary(bnd);
        MixResult mixResult = mix();
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * nodeValueDimension];
        int i = 0;
        if (getDiffOrder() > 1) {
            throw new UnsupportedOperationException();
        }

        double[][] shapeFunctionValues = mixResult.getShapeFunctionValues();
        for (MFNode node : mixResult.getNodes()) {
            double[] value = node.getValue();

            double sv = shapeFunctionValues[0][i];
            for (int valueDim = 0; valueDim < nodeValueDimension; valueDim++) {
                output[valueDim] += value[valueDim] * sv;
                if (getDiffOrder() >= 1) {
                    for (int varDim = 0; varDim < VARIABLE_DIMENSION; varDim++) {
                        double s_p = shapeFunctionValues[varDim + 1][i];
                        output[(valueDim + 1) * nodeValueDimension + varDim] += s_p * value[valueDim];
                    }
                }
            }
            i++;
        }
        return output;
    }
}
