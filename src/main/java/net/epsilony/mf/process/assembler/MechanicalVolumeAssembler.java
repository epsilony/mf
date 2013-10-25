/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.cons_law.ConstitutiveLaw;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalVolumeAssembler
        extends AbstractAssembler {

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
        double[] volumnForce = load;
        double[] lv = testShapeFunctionValues[0];

        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int rowIndex = nodesAssemblyIndes.getQuick(i);
            int row = rowIndex * valueDimension;

            if (volumnForce != null) {
                double lv_i = lv[i];
                for (int dim = 0; dim < valueDimension; dim++) {
                    mainVector.add(row + dim, 0, weight * volumnForce[dim] * lv_i);
                }
            }

            double[][] lefts = getLefts(i);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int colIndex = nodesAssemblyIndes.getQuick(j);

                if (mainMatrix.isUpperSymmetric() && colIndex < rowIndex) {
                    continue;
                }
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
        fillLeftOrRightsCache(leftsCache, i, testShapeFunctionValues);
        return leftsCache;
    }

    private double[][] getRights(int j) {
        fillLeftOrRightsCache(rightsCache, j, trialShapeFunctionValues);
        return rightsCache;
    }

    private void addToMainMatrix(double[][] lefts, int rowUpLeft, double[][] rights, int colUpLeft) {
        for (int rowDim = 0; rowDim < valueDimension; rowDim++) {
            int row = rowUpLeft + rowDim;
            for (int colDim = 0; colDim < valueDimension; colDim++) {
                int col = colUpLeft + colDim;
                if (mainMatrix.isUpperSymmetric() && col < row) {
                    continue;
                }
                mainMatrix.add(row, col, weight * multConstitutiveLaw(lefts[rowDim], rights[colDim]));
            }
        }
    }

    private void initCaches() {
        int dimension = valueDimension;
        int[] cachesSizes = new int[]{1, 3, 6};
        leftsCache = new double[dimension][cachesSizes[dimension - 1]];
        rightsCache = new double[dimension][cachesSizes[dimension - 1]];
        multConstitutiveLawCache = new double[cachesSizes[dimension - 1]];
    }

    private void fillLeftOrRightsCache(double[][] cache, int index, double[][] shapeFunctionValues) {
        switch (valueDimension) {
            case 1:
                cache[0][0] = shapeFunctionValues[1][index];
                break;
            case 2:
                cache[0][0] = shapeFunctionValues[1][index];
                cache[1][1] = shapeFunctionValues[2][index];
                cache[0][2] = shapeFunctionValues[2][index];
                cache[1][2] = shapeFunctionValues[1][index];
                break;
            case 3:
                cache[0][0] = shapeFunctionValues[1][index];
                cache[1][1] = shapeFunctionValues[2][index];
                cache[2][2] = shapeFunctionValues[3][index];
                cache[0][3] = shapeFunctionValues[2][index];
                cache[1][3] = shapeFunctionValues[1][index];
                cache[1][4] = shapeFunctionValues[3][index];
                cache[2][4] = shapeFunctionValues[2][index];
                cache[2][5] = shapeFunctionValues[1][index];
                cache[0][5] = shapeFunctionValues[3][index];
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
