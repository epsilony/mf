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
package net.epsilony.mf.integrate.unit;

import net.epsilony.mf.model.load.LoadInputType;
import net.epsilony.mf.util.DataHolder;
import net.epsilony.mf.util.DataType;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomUnitQuadraturePoint<T extends GeomUnit> extends SpatialQuadraturePoint implements DataHolder {
    T geomUnit;

    public T getGeomUnit() {
        return geomUnit;
    }

    public void setGeomUnit(T geomUnit) {
        this.geomUnit = geomUnit;
    }

    @Override
    public Object getValue(DataType dataType) {
        if (dataType == LoadInputType.GEOM_UNIT) {
            return geomUnit;
        } else if (dataType == LoadInputType.SPATIAL) {
            return coord;
        }
        return null;
    }
}
