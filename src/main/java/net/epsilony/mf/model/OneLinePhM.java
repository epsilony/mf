/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneLinePhM implements PhysicalModel {

    RawPhysicalModel rawPhysicalModel = new RawPhysicalModel();

    public OneLinePhM() {
        init();
    }

    private void init() {
        rawPhysicalModel.setDimension(1);
        Line root = new Line(new MFNode(new double[2]));
        root.setSucc(new Line(new MFNode(new double[2])));
        rawPhysicalModel.setGeomRoot(root);
        rawPhysicalModel.setLoadMap(new HashMap<GeomUnit, MFLoad>());
    }

    @Override
    public int getDimension() {
        return rawPhysicalModel.getDimension();
    }

    @Override
    public void setDimension(int dimension) {
        if (dimension != 1) {
            throw new IllegalArgumentException("only support 1d not " + dimension);
        }
        rawPhysicalModel.setDimension(dimension);
    }

    @Override
    public Map<GeomUnit, MFLoad> getLoadMap() {
        return rawPhysicalModel.getLoadMap();
    }

    @Override
    public GeomUnit getGeomRoot() {
        return rawPhysicalModel.getGeomRoot();
    }

    public void setVolumeLoad(MFLoad load) {
        rawPhysicalModel.setVolumeLoad(load);
    }

    public MFLoad getVolumeLoad() {
        return rawPhysicalModel.getVolumeLoad();
    }

    public Node getVertex(boolean start) {
        Line line = (Line) rawPhysicalModel.getGeomRoot();
        Node node = start ? line.getStart() : line.getEnd();
        return node;
    }
}
