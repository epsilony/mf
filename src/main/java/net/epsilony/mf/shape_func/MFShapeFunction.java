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

import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;

import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.tb.analysis.WithDiffOrder;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFShapeFunction extends WithDiffOrder {

    void setInfluenceRadiusGetter(IntToDoubleFunction influenceRadiusGetter);

    void setCoordsGetter(IntFunction<double[]> nodesCoordsGetter);

    void setInputSizeSupplier(IntSupplier inputSizeSupplier);

    void setPosition(double[] position);

    void setSpatialDimension(int spatialDimension);

    PartialTuple values();
}
