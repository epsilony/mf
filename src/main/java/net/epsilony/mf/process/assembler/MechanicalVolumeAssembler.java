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
        ShapeFunctionValue trialValue = assemblyInput.getT2Value().getTrialValue();
        ShapeFunctionValue testValue = assemblyInput.getT2Value().getTestValue();
        for (int i = 0; i < testValue.getNodesSize(); i++) {
            int rowIndex = testValue.getNodeAssemblyIndex(i);
            int row = rowIndex * valueDimension;
            double[][] lefts = getLefts(testValue, i);

            for (int j = 0; j < trialValue.getNodesSize(); j++) {
                int colIndex = trialValue.getNodeAssemblyIndex(j);
                int col = colIndex * valueDimension;
                double[][] rights = getRights(trialValue, j);

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

    private double[][] getLefts(ShapeFunctionValue testValue, int i) {
        fillCache(testValue, leftsCache, i);
        return leftsCache;
    }

    private double[][] getRights(ShapeFunctionValue trailValue, int j) {
        fillCache(trailValue, rightsCache, j);
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

    private void fillCache(ShapeFunctionValue value, double[][] cache, int index) {
        switch (valueDimension) {
        case 1:
            cache[0][0] = value.getValue(index, 1);
            break;
        case 2:
            cache[0][0] = value.getValue(index, 1);
            cache[1][1] = value.getValue(index, 2);
            cache[0][2] = value.getValue(index, 2);
            cache[1][2] = value.getValue(index, 1);
            break;
        case 3:
            cache[0][0] = value.getValue(index, 1);
            cache[1][1] = value.getValue(index, 2);
            cache[2][2] = value.getValue(index, 3);
            cache[0][3] = value.getValue(index, 2);
            cache[1][3] = value.getValue(index, 1);
            cache[1][4] = value.getValue(index, 3);
            cache[2][4] = value.getValue(index, 2);
            cache[2][5] = value.getValue(index, 1);
            cache[0][5] = value.getValue(index, 3);
            break;
        default:
            throw new IllegalStateException();
        }
    }
}
