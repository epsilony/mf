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
import net.epsilony.mf.model.geom.MFGeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawPhysicalModel implements PhysicalModel {

    protected Map<Object, GeomPointLoad> loadMap;
    protected MFGeomUnit geomRoot;
    protected int spatialDimension;
    protected int valueDimension;

    @Override
    public Map<Object, GeomPointLoad> getLoadMap() {
        return loadMap;
    }

    public void setLoadMap(Map<Object, GeomPointLoad> loadMap) {
        this.loadMap = loadMap;
    }

    @Override
    public MFGeomUnit getBoundaryRoot() {
        return geomRoot;
    }

    public void setGeomRoot(MFGeomUnit geomRoot) {
        this.geomRoot = geomRoot;
    }

    public void setVolumeLoad(GeomPointLoad load) {
        setVolumeLoad(this, load);
    }

    public GeomPointLoad getVolumeLoad() {
        return getVolumeLoad(this);
    }

    public static void setVolumeLoad(PhysicalModel model, GeomPointLoad load) {
        if (null == load) {
            model.getLoadMap().remove(model.getBoundaryRoot());
        } else {
            model.getLoadMap().put(model.getBoundaryRoot(), load);
        }
    }

    public static GeomPointLoad getVolumeLoad(PhysicalModel model) {
        return model.getLoadMap().get(model.getBoundaryRoot());
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
