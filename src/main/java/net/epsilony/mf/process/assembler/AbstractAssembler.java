/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractAssembler implements Assembler {

    public static final int DEFAULT_SPATIAL_DIMENSION = 2;
    public static final int DEFAULT_VALUE_DIMENSION = 2;
    protected int nodesNum;
    protected int spatialDimension = DEFAULT_SPATIAL_DIMENSION;
    protected int valueDimension = DEFAULT_VALUE_DIMENSION;
    transient protected double[] load;
    transient protected boolean[] loadValidity;
    transient protected MFMatrix mainMatrix;
    transient protected MFMatrix mainVector;
    transient protected TIntArrayList nodesAssemblyIndes;
    transient protected double[][] trialShapeFunctionValues;
    transient protected double[][] testShapeFunctionValues;
    transient protected double weight;
    int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public double[] getLoad() {
        return load;
    }

    public boolean[] getLoadValidity() {
        return loadValidity;
    }

    public void setLoadValidity(boolean[] loadValidity) {
        this.loadValidity = loadValidity;
    }

    public MFMatrix getMainMatrix() {
        return mainMatrix;
    }

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        int requiredMatrixSize = getRequiredMatrixSize();
        if (mainMatrix.numCols() < requiredMatrixSize || mainMatrix.numRows() < requiredMatrixSize) {
            throw new IllegalArgumentException();
        }
        this.mainMatrix = mainMatrix;
    }

    public MFMatrix getMainVector() {
        int requiredMatrixSize = getRequiredMatrixSize();
        if (mainMatrix.numCols() != 1 || requiredMatrixSize != mainMatrix.numRows()) {
            throw new IllegalArgumentException();
        }
        return mainVector;
    }

    @Override
    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    @Override
    public int getRequiredMatrixSize() {
        return getValueDimension() * nodesNum;
    }

    @Override
    public void setLoad(double[] value, boolean[] validity) {
        this.load = value;
        this.loadValidity = validity;
    }

    @Override
    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    public int getNodesNum() {
        return nodesNum;
    }

    @Override
    public void setNodesAssemblyIndes(TIntArrayList nodesAssemblyIndes) {
        this.nodesAssemblyIndes = nodesAssemblyIndes;
    }

    @Override
    public void setTrialShapeFunctionValues(double[][] shapeFunValues) {
        trialShapeFunctionValues = shapeFunValues;
    }

    @Override
    public void setTestShapeFunctionValues(double[][] shapeFunValues) {
        testShapeFunctionValues = shapeFunValues;
    }

    public TIntArrayList getNodesAssemblyIndes() {
        return nodesAssemblyIndes;
    }

    public double[][] getTrialShapeFunctionValues() {
        return trialShapeFunctionValues;
    }

    public double[][] getTestShapeFunctionValues() {
        return testShapeFunctionValues;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    @Override
    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    public int getValueDimension() {
        return valueDimension;
    }

    @Override
    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, "
                        + "main matrix size: %d}",
                        getNodesNum(),
                        getSpatialDimension(),
                        getRequiredMatrixSize());
    }
}
