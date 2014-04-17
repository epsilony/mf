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

import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainPhysicalModel implements PhysicalModel {

    RawPhysicalModel rawPhysicalModel = new RawPhysicalModel();

    public ChainPhysicalModel() {
        init();
    }

    private void init() {
        rawPhysicalModel.setSpatialDimension(1);
        Line head = new Line(new MFNode(new double[2]));
        Line succ = new Line(new MFNode(new double[2]));
        Segment2DUtils.link(head, succ);
        rawPhysicalModel.setGeomRoot(new Chain(head));
        rawPhysicalModel.setLoadMap(new HashMap<Object, GeomPointLoad>());
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
    public Map<Object, GeomPointLoad> getLoadMap() {
        return rawPhysicalModel.getLoadMap();
    }

    @Override
    public GeomUnit getGeomRoot() {
        return rawPhysicalModel.getGeomRoot();
    }

    public void setVolumeLoad(GeomPointLoad load) {
        rawPhysicalModel.setVolumeLoad(load);
    }

    public GeomPointLoad getVolumeLoad() {
        return rawPhysicalModel.getVolumeLoad();
    }

    public Node getTerminalVertex(boolean start) {
        Chain chain = (Chain) rawPhysicalModel.getGeomRoot();
        Node node = start ? chain.getHead().getStart() : chain.getLast().getStart();
        return node;
    }

    public void setLoadOnTerminalVertex(boolean start, GeomPointLoad load) {
        Node node = getTerminalVertex(start);
        getLoadMap().put(node, load);

    }

    public void setTerminalPosition(boolean start, double position) {
        Node node = getTerminalVertex(start);
        node.getCoord()[0] = position;
    }

    public double getTerminalPoistion(boolean start) {
        return getTerminalVertex(start).getCoord()[0];
    }

    public void setChain(Chain chain) {
        rawPhysicalModel.setGeomRoot(chain);
    }

    public Chain getChain() {
        return (Chain) rawPhysicalModel.getGeomRoot();
    }
}
