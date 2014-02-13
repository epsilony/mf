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
package net.epsilony.mf.integrate.convertor;

import java.util.Map;

import net.epsilony.mf.model.load.Load;
import net.epsilony.mf.model.load.LoadInputType;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.model.load.Loads;
import net.epsilony.mf.util.DataHolder;
import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomUnitLoadMapBasedLoadDriver<T extends LoadValue> implements Convertor<DataHolder, T> {

    Map<GeomUnit, Load<? extends T>> loadMap;

    @Override
    public T convert(DataHolder input) {
        Load<? extends T> load = loadMap.get(input.getValue(LoadInputType.GEOM_UNIT));
        return Loads.getLoadValue(load, input);
    }
}
