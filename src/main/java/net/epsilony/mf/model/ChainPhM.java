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
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainPhM implements PhysicalModel {

    RawPhysicalModel rawPhysicalModel = new RawPhysicalModel();

    public ChainPhM() {
        init();
    }

    private void init() {
        rawPhysicalModel.setDimension(1);
        Line head = new Line(new MFNode(new double[2]));
        Line succ = new Line(new MFNode(new double[2]));
        Segment2DUtils.link(head, succ);
        rawPhysicalModel.setGeomRoot(new Chain(head));
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
        Chain chain = (Chain) rawPhysicalModel.getGeomRoot();
        Node node = start ? chain.getHead().getStart() : chain.getLast().getEnd();
        return node;
    }

    public void setChain(Chain chain) {
        rawPhysicalModel.setGeomRoot(chain);
    }

    public Chain getChain() {
        return (Chain) rawPhysicalModel.getGeomRoot();
    }
}
