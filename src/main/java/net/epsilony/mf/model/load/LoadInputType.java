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
package net.epsilony.mf.model.load;

import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public enum LoadInputType implements DataType {
SPATIAL(double[].class, "spactial coordinate"), GEOM_UNIT(GeomUnit.class, "geometric unit"), GEOM_UNIT_PARAMETERS(
        double[].class, "geometric unit parameters");
private Class<?> valueClass;
private String description;

LoadInputType(Class<?> valueClass, String description) {
    this.valueClass = valueClass;
    this.description = description;
}

@Override
public Class<?> getValueClass() {
    return valueClass;
}

@Override
public String toString() {
    return getClass().getSimpleName() + "[value class:" + valueClass + ", " + description + "]";
}
}
