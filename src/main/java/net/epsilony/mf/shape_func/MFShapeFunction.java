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

package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.Serializable;
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.analysis.Dimensional;
import net.epsilony.tb.analysis.WithDiffOrder;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFShapeFunction extends IntIdentity, WithDiffOrder, Serializable, Dimensional {

    void setNodes(List<MFNode> nodes);

    void setDistancesToPosition(TDoubleArrayList[] distances);

    void setPosition(double[] position);

    ShapeFunctionValue values();

    boolean isValueIndependent();
}
