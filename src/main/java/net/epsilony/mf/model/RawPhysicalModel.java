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

package net.epsilony.mf.model;

import java.util.Map;

import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.tb.solid.GeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawPhysicalModel implements PhysicalModel {

    protected Map<GeomUnit, GeomPointLoad<? extends LoadValue>> loadMap;
    protected GeomUnit geomRoot;
    protected int spatialDimension;
    protected int valueDimension;

    @Override
    public Map<GeomUnit, GeomPointLoad<? extends LoadValue>> getLoadMap() {
        return loadMap;
    }

    public void setLoadMap(Map<GeomUnit, GeomPointLoad<? extends LoadValue>> loadMap) {
        this.loadMap = loadMap;
    }

    @Override
    public GeomUnit getGeomRoot() {
        return geomRoot;
    }

    public void setGeomRoot(GeomUnit geomRoot) {
        this.geomRoot = geomRoot;
    }

    public void setVolumeLoad(GeomPointLoad<? extends LoadValue> load) {
        setVolumeLoad(this, load);
    }

    public GeomPointLoad<? extends LoadValue> getVolumeLoad() {
        return getVolumeLoad(this);
    }

    public static void setVolumeLoad(PhysicalModel model, GeomPointLoad<? extends LoadValue> load) {
        if (null == load) {
            model.getLoadMap().remove(model.getGeomRoot());
        } else {
            model.getLoadMap().put(model.getGeomRoot(), load);
        }
    }

    public static GeomPointLoad<? extends LoadValue> getVolumeLoad(PhysicalModel model) {
        return model.getLoadMap().get(model.getGeomRoot());
    }

    @Override
    public int getSpatialDimension() {
        return spatialDimension;
    }

    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    @Override
    public int getValueDimension() {
        return valueDimension;
    }

    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }
}
