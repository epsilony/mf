/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;
import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.NeedPreparation;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface Assembler extends IntIdentity, NeedPreparation, Serializable {

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

    void setNodesNum(int nodesNum);

    int getNodesNum();

    void setMatrixDense(boolean dense);

    boolean isMatrixDense();

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmetric();

    void setUpperSymmetric(boolean upperSymmetric);

    void mergeWithBrother(Assembler brother);

    void setValueDimension(int valueDimension);

    int getValueDimension();

    void setSpatialDimension(int spacialDimension);

    int getSpatialDimension();
}
