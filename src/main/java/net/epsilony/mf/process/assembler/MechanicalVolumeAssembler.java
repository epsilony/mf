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

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.load.LoadValue;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalVolumeAssembler extends AbstractAssembler<AssemblyInput<? extends LoadValue>> {

    protected ConstitutiveLaw constitutiveLaw;
    double[][] leftsCache, rightsCache;
    double[] multConstitutiveLawCache;

    public MechanicalVolumeAssembler() {
        initCaches();
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    @Override
    public void assemble() {
        TTValue ttValue = assemblyInput.getTTValue();
        for (int i = 0; i < ttValue.getNodesSize(); i++) {
            int rowIndex = ttValue.getNodeAssemblyIndex(i);
            int row = rowIndex * valueDimension;

            double[][] lefts = getLefts(i);
            for (int j = 0; j < ttValue.getNodesSize(); j++) {
                int colIndex = ttValue.getNodeAssemblyIndex(j);
                int col = colIndex * valueDimension;
                double[][] rights = getRights(j);

                addToMainMatrix(lefts, row, rights, col);
            }
        }
    }

    protected double multConstitutiveLaw(double[] left, double[] right) {
        double[] calcStress = constitutiveLaw.calcStressByEngineeringStrain(right, multConstitutiveLawCache);
        double result = 0;
        for (int i = 0; i < calcStress.length; i++) {
            result += left[i] * calcStress[i];
        }
        return result;
    }

    @Override
    public void setValueDimension(int dimension) {
        super.setValueDimension(dimension);
        initCaches();
    }

    private double[][] getLefts(int i) {
        fillLeftCache(leftsCache, i);
        return leftsCache;
    }

    private double[][] getRights(int j) {
        fillRightCache(rightsCache, j);
        return rightsCache;
    }

    private void addToMainMatrix(double[][] lefts, int rowUpLeft, double[][] rights, int colUpLeft) {
        double weight = assemblyInput.getWeight();
        for (int rowDim = 0; rowDim < valueDimension; rowDim++) {
            int row = rowUpLeft + rowDim;
            for (int colDim = 0; colDim < valueDimension; colDim++) {
                int col = colUpLeft + colDim;
                mainMatrix.add(row, col, weight * multConstitutiveLaw(lefts[rowDim], rights[colDim]));
            }
        }
    }

    private void initCaches() {
        int dimension = valueDimension;
        int[] cachesSizes = new int[] { 1, 3, 6 };
        leftsCache = new double[dimension][cachesSizes[dimension - 1]];
        rightsCache = new double[dimension][cachesSizes[dimension - 1]];
        multConstitutiveLawCache = new double[cachesSizes[dimension - 1]];
    }

    private void fillLeftCache(double[][] cache, int index) {
        TTValue ttValue = assemblyInput.getTTValue();
        switch (valueDimension) {
        case 1:
            cache[0][0] = ttValue.getTestValue(index, 1);
            break;
        case 2:
            cache[0][0] = ttValue.getTestValue(index, 1);
            cache[1][1] = ttValue.getTestValue(index, 2);
            cache[0][2] = ttValue.getTestValue(index, 2);
            cache[1][2] = ttValue.getTestValue(index, 1);
            break;
        case 3:
            cache[0][0] = ttValue.getTestValue(index, 1);
            cache[1][1] = ttValue.getTestValue(index, 2);
            cache[2][2] = ttValue.getTestValue(index, 3);
            cache[0][3] = ttValue.getTestValue(index, 2);
            cache[1][3] = ttValue.getTestValue(index, 1);
            cache[1][4] = ttValue.getTestValue(index, 3);
            cache[2][4] = ttValue.getTestValue(index, 2);
            cache[2][5] = ttValue.getTestValue(index, 1);
            cache[0][5] = ttValue.getTestValue(index, 3);
            break;
        default:
            throw new IllegalStateException();
        }
    }

    private void fillRightCache(double[][] cache, int index) {
        TTValue ttValue = assemblyInput.getTTValue();
        switch (valueDimension) {
        case 1:
            cache[0][0] = ttValue.getTrialValue(index, 1);
            break;
        case 2:
            cache[0][0] = ttValue.getTrialValue(index, 1);
            cache[1][1] = ttValue.getTrialValue(index, 2);
            cache[0][2] = ttValue.getTrialValue(index, 2);
            cache[1][2] = ttValue.getTrialValue(index, 1);
            break;
        case 3:
            cache[0][0] = ttValue.getTrialValue(index, 1);
            cache[1][1] = ttValue.getTrialValue(index, 2);
            cache[2][2] = ttValue.getTrialValue(index, 3);
            cache[0][3] = ttValue.getTrialValue(index, 2);
            cache[1][3] = ttValue.getTrialValue(index, 1);
            cache[1][4] = ttValue.getTrialValue(index, 3);
            cache[2][4] = ttValue.getTrialValue(index, 2);
            cache[2][5] = ttValue.getTrialValue(index, 1);
            cache[0][5] = ttValue.getTrialValue(index, 3);
            break;
        default:
            throw new IllegalStateException();
        }
    }
}
