/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.HashMap;
import net.epsilony.mf.model.load.MFLoad;
import java.util.Map;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawPhysicalModel implements PhysicalModel {

    int dimension;
    Map<GeomUnit, MFLoad> loadMap;
    GeomUnit geomRoot;

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public Map<GeomUnit, MFLoad> getLoadMap() {
        return loadMap;
    }

    public void setLoadMap(Map<GeomUnit, MFLoad> loadMap) {
        this.loadMap = loadMap;
    }

    @Override
    public GeomUnit getGeomRoot() {
        return geomRoot;
    }

    public void setGeomRoot(GeomUnit geomRoot) {
        this.geomRoot = geomRoot;
    }

    public void setVolumeLoad(MFLoad load) {
        setVolumeLoad(this, load);
    }

    public MFLoad getVolumeLoad() {
        return getVolumeLoad(this);
    }

    public static void setVolumeLoad(PhysicalModel model, MFLoad load) {
        model.getLoadMap().put(model.getGeomRoot(), load);
    }

    public static MFLoad getVolumeLoad(PhysicalModel model) {
        return model.getLoadMap().get(model.getGeomRoot());
    }
}