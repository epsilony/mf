/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.synchron.SynchronizedClonable;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface Assembler<T extends Assembler> extends NeedPreparation, SynchronizedClonable<T> {

    void assembleVolume();

    void assembleDirichlet();

    void assembleNeumann();

    void setWeight(double weight);

    void setTrialShapeFunctionValues(TIntArrayList nodesAssemblyIndes, double[][] shapeFunValues);

    void setTestShapeFunctionValues(TIntArrayList nodesAssemblyIndes, double[][] shapeFunValues);

    void setLoad(double[] value, boolean[] validity);

    int getVolumeDiffOrder();

    int getNeumannDiffOrder();

    int getDirichletDiffOrder();

    void setNodesNum(int nodesNum);

    int getNodesNum();

    void setMatrixDense(boolean dense);

    boolean isMatrixDense();

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmetric();

    void mergeWithBrother(Assembler brother);

    int getNodeValueDimension();
}
