/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.analysis.Dimensional;
import net.epsilony.tb.synchron.SynchronizedClonable;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface Assembler<T extends Assembler> extends Dimensional, NeedPreparation, SynchronizedClonable<T> {

    void assembleVolume();

    void assembleDirichlet();

    void assembleNeumann();

    void setWeight(double weight);

    void setNodesAssemblyIndes(TIntArrayList nodesAssemblyIndes);

    void setTrialShapeFunctionValues(double[][] shapeFunValues);

    void setTestShapeFunctionValues(double[][] shapeFunValues);

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
}
