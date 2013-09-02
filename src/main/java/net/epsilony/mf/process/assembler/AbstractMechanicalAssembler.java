/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import no.uib.cipr.matrix.DenseVector;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMechanicalAssembler
        extends AbstractAssembler implements MechanicalAssembler {

    protected ConstitutiveLaw constitutiveLaw;
    boolean upperSymmetric = true;
    double[][] leftsCache, rightsCache;
    double[] multConstitutiveLawCache;

    public AbstractMechanicalAssembler() {
        initCaches(dimension);
    }

    public void setUpperSymmetric(boolean upperSymmetric) {
        this.upperSymmetric = upperSymmetric;
    }

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    @Override
    public boolean isUpperSymmetric() {
        return upperSymmetric;
    }

    @Override
    public void assembleNeumann() {
        DenseVector vec = mainVector;
        double[] neumannVal = load;
        double[] vs = testShapeFunctionValues[0];
        TIntArrayList indes = nodesAssemblyIndes;

        for (int i = 0; i < indes.size(); i++) {
            int vecIndex = indes.getQuick(i) * dimension;
            double v = vs[i];
            for (int dm = 0; dm < dimension; dm++) {
                vec.add(vecIndex + dm, v * neumannVal[dm] * weight);
            }
        }
    }

    @Override
    public void assembleVolume() {
        double[] volumnForce = load;
        double[] lv = testShapeFunctionValues[0];



        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int rowIndex = nodesAssemblyIndes.getQuick(i);
            int row = rowIndex * dimension;

            if (volumnForce != null) {
                double lv_i = lv[i];
                for (int dim = 0; dim < dimension; dim++) {
                    mainVector.add(row + dim, weight * volumnForce[dim] * lv_i);
                }
            }

            double[][] lefts = getLefts(i);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int colIndex = nodesAssemblyIndes.getQuick(j);

                if (upperSymmetric && colIndex < rowIndex) {
                    continue;
                }
                int col = colIndex * dimension;
                double[][] rights = getRights(j);

                addToMainMatrix(lefts, row, rights, col);
            }
        }
    }

    @Override
    public int getDirichletDiffOrder() {
        return 0;
    }

    @Override
    public int getVolumeDiffOrder() {
        return 1;
    }

    protected double multConstitutiveLaw(double[] left, double[] right) {
        double[] calcStress = constitutiveLaw.calcStressByEngineering(right, multConstitutiveLawCache);
        double result = 0;
        for (int i = 0; i < calcStress.length; i++) {
            result += left[i] * calcStress[i];
        }
        return result;
    }

    @Override
    public void setDimension(int dimension) {
        super.setDimension(dimension);
        initCaches(dimension);
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
        for (int rowDim = 0; rowDim < dimension; rowDim++) {
            int row = rowUpLeft + rowDim;
            for (int colDim = 0; colDim < dimension; colDim++) {
                int col = colUpLeft + colDim;
                if (upperSymmetric && col < row) {
                    continue;
                }
                mainMatrix.add(row, col, weight * multConstitutiveLaw(lefts[rowDim], rights[colDim]));
            }
        }
    }

    private void initCaches(int dimension) {
        int[] cachesSizes = new int[]{1, 3, 6};
        leftsCache = new double[dimension][cachesSizes[dimension - 1]];
        rightsCache = new double[dimension][cachesSizes[dimension - 1]];
        multConstitutiveLawCache = new double[cachesSizes[dimension - 1]];
    }

    private void fillLeftOrRightsCache(double[][] cache, int index, double[][] shapeFunctionValues) {
        switch (dimension) {
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
