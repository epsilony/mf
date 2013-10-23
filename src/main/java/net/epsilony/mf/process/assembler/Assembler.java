/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.IntIdentity;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface Assembler extends IntIdentity, Serializable {

    void assemble();

    void setWeight(double weight);

    void setNodesAssemblyIndes(TIntArrayList nodesAssemblyIndes);

    void setTrialShapeFunctionValues(double[][] shapeFunValues);

    void setTestShapeFunctionValues(double[][] shapeFunValues);

    void setLoad(double[] value, boolean[] validity);

    void setNodesNum(int nodesNum);

    int getRequiredMatrixSize();

    void setMainMatrix(MFMatrix matrix);

    void setMainVector(MFMatrix vector);

    void setValueDimension(int valueDimension);

    void setSpatialDimension(int spacialDimension);
}
