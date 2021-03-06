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
package net.epsilony.mf.integrate.integrator;

import java.util.Map;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.model.geom.MFGeomUnit;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LoadValueFunction implements Function<GeomPoint, LoadValue> {

    Map<MFGeomUnit, GeomPointLoad> loadMap;

    public Map<MFGeomUnit, GeomPointLoad> getLoadMap() {
        return loadMap;
    }

    public void setLoadMap(Map<MFGeomUnit, GeomPointLoad> loadMap) {
        this.loadMap = loadMap;
    }

    @Override
    public LoadValue apply(GeomPoint t) {
        GeomPointLoad geomPointLoad = loadMap.get(t.getLoadKey());
        if (null == geomPointLoad) {
            return null;
        }
        return geomPointLoad.calcLoad(t);
    }

}
