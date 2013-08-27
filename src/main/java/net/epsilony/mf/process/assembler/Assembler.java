/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.analysis.Dimensional;
import net.epsilony.tb.CloneFactory;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface Assembler<T extends Assembler> extends Dimensional, NeedPreparation, CloneFactory<T> {

    void assembleVolume();

    void assembleDirichlet();

    void assembleNeumann();

    void setWeight(double weight);

    double getWeight();

    void setNodesAssemblyIndes(TIntArrayList nodesAssemblyIndes);

    TIntArrayList getNodesAssemblyIndes();

    void setTrialShapeFunctionValues(double[][] shapeFunValues);

    double[][] getTrialShapeFunctionValues();

    void setTestShapeFunctionValues(double[][] shapeFunValues);

    double[][] getTestShapeFunctionValues();

    void setLoad(double[] value, boolean[] validity);

    double[] getLoad();

    boolean[] getLoadValidity();

    int getVolumeDiffOrder();

    int getNeumannDiffOrder();

    int getDirichletDiffOrder();

    void setNodesNum(int nodesNum);

    int getNodesNum();

    void setMatrixDense(boolean dense);

    boolean isMatrixDense();

    void setMainMatrix(Matrix mainMatrix);

    Matrix getMainMatrix();

    void setMainVector(DenseVector mainVector);

    DenseVector getMainVector();

    boolean isUpperSymmetric();

    void mergeWithBrother(Assembler brother);
}
