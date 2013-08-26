/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.UpperSymmDenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractAssembler<T extends Assembler<T>> implements Assembler<T> {

    protected boolean dense;
    protected double[] load;
    protected boolean[] loadValidity;
    protected Matrix mainMatrix;
    protected DenseVector mainVector;
    protected TIntArrayList nodesAssemblyIndes;
    protected int nodesNum;
    protected double[][] trialShapeFunctionValues;
    protected double[][] testShapeFunctionValues;
    protected double weight;

    @Override
    public Matrix getMainMatrix() {
        return mainMatrix;
    }

    @Override
    public DenseVector getMainVector() {
        return mainVector;
    }

    @Override
    public int getNeumannDiffOrder() {
        return 0;
    }

    @Override
    public void prepare() {
        initMainMatrixVector();
    }

    protected final void initMainMatrixVector() {
        int numRowCol = getMainMatrixSize();
        if (dense) {
            if (isUpperSymmetric()) {
                mainMatrix = new UpperSymmDenseMatrix(numRowCol);
            } else {
                mainMatrix = new DenseMatrix(numRowCol, numRowCol);
            }
        } else {
            mainMatrix = new FlexCompRowMatrix(numRowCol, numRowCol);
        }
        mainVector = new DenseVector(numRowCol);
    }

    protected int getMainMatrixSize() {
        return getNodeValueDimension() * nodesNum;
    }

    @Override
    public boolean isMatrixDense() {
        return dense;
    }

    @Override
    public void mergeWithBrother(Assembler otherAssembler) {
        if (otherAssembler.isUpperSymmetric() != isUpperSymmetric()) {
            throw new IllegalArgumentException("the assembler to add in should be with same symmetricity");
        }
        Matrix otherMat = otherAssembler.getMainMatrix();
        mainMatrix.add(otherMat);
        mainVector.add(otherAssembler.getMainVector());
    }

    @Override
    public void setLoad(double[] value, boolean[] validity) {
        this.load = value;
        this.loadValidity = validity;
    }

    @Override
    public void setMatrixDense(boolean dense) {
        this.dense = dense;
    }

    @Override
    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    @Override
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

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
