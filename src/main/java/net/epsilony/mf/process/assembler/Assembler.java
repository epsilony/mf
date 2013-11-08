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
