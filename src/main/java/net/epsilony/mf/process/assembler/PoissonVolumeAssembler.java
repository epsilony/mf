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

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PoissonVolumeAssembler extends AbstractAssembler {

    public PoissonVolumeAssembler() {
        valueDimension = 1;
    }

    @Override
    public void assemble() {
        for (int testPos = 0; testPos < nodesAssemblyIndes.size(); testPos++) {
            assembleVolumeVectorElem(testPos);
            for (int trialPos = 0; trialPos < nodesAssemblyIndes.size(); trialPos++) {
                assembleVolumeMatrixElem(testPos, trialPos);
            }
        }
    }

    private void assembleVolumeVectorElem(int testPos) {
        if (load == null) {
            return;
        }
        int row = nodesAssemblyIndes.getQuick(testPos);
        mainVector.add(row, 0, testShapeFunctionValues[0][testPos] * weight * load[0]);
    }

    private void assembleVolumeMatrixElem(int testPos, int trialPos) {

        int col = nodesAssemblyIndes.getQuick(trialPos);
        int row = nodesAssemblyIndes.getQuick(testPos);
        if (mainMatrix.isUpperSymmetric() && col < row) {
            return;
        }
        double value = 0;
        for (int spatialDim = 0; spatialDim < spatialDimension; spatialDim++) {
            value += testShapeFunctionValues[spatialDim + 1][testPos] * trialShapeFunctionValues[spatialDim + 1][trialPos];
        }
        mainMatrix.add(row, col, value * weight);
    }

    @Override
    public int getValueDimension() {
        return super.getValueDimension();
    }

    @Override
    public void setValueDimension(int valueDimension) {
        if (valueDimension != 1) {
            throw new IllegalArgumentException();
        }
        super.setValueDimension(valueDimension);
    }
}
