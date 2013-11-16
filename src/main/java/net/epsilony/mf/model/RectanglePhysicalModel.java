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

import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.tb.solid.GeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectanglePhysicalModel extends MFRectangle implements PhysicalModel {

    private static final int SPATIAL_DIMENSION = 2;

    RawPhysicalModel rawPhysicalModel;

    public RectanglePhysicalModel() {
        super();
        rawPhysicalModel = new FacetModel();
        rawPhysicalModel.setSpatialDimension(SPATIAL_DIMENSION);
        rawPhysicalModel.setGeomRoot(facet);
        Map<GeomUnit, MFLoad> loadMap = new HashMap<>();
        rawPhysicalModel.setLoadMap(loadMap);
    }

    @Override
    public int getSpatialDimension() {
        return rawPhysicalModel.getSpatialDimension();
    }

    @Override
    public int getValueDimension() {
        return rawPhysicalModel.getValueDimension();
    }

    public void setValueDimension(int valueDimension) {
        rawPhysicalModel.setValueDimension(valueDimension);
    }

    @Override
    public Map<GeomUnit, MFLoad> getLoadMap() {
        return rawPhysicalModel.getLoadMap();
    }

    public void setLoadMap(Map<GeomUnit, MFLoad> loadMap) {
        rawPhysicalModel.setLoadMap(loadMap);
    }

    public void setEdgeLoad(MFRectangleEdge edge, MFLoad load) {
        rawPhysicalModel.getLoadMap().put(getEdgeLine(edge), load);
    }

    public void setVolumeLoad(MFLoad load) {
        rawPhysicalModel.getLoadMap().put(rawPhysicalModel.getGeomRoot(), load);
    }

    public MFLoad getVolumeLoad() {
        return rawPhysicalModel.getLoadMap().get(rawPhysicalModel.getGeomRoot());
    }

    @Override
    public GeomUnit getGeomRoot() {
        return rawPhysicalModel.getGeomRoot();
    }

    public RawPhysicalModel getRawPhysicalModel() {
        return rawPhysicalModel;
    }
}
