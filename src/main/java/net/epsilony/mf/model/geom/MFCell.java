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
package net.epsilony.mf.model.geom;

import java.util.function.Consumer;

import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface MFCell extends MFGeomUnit {
    int vertesSize();

    MFEdge getVertexEdge(int i);

    void setVertexEdge(int i, MFEdge edge);

    default MFEdge getVertexEdgeCycly(int i) {
        return getVertexEdge(i % vertesSize());
    }

    default void setVertexEdgeCycly(int i, MFEdge edge) {
        setVertexEdge(i % vertesSize(), edge);
    }

    default void connectEdges() {
        for (int i = 0; i < vertesSize(); i++) {
            final MFEdge vertexEdge = getVertexEdge(i);
            vertexEdge.connectSucc(getVertexEdgeCycly(i + 1));
            vertexEdge.setCell(this);
        }
    }

    default Node getVertex(int i) {
        return getVertexEdge(i).getStart();
    }

    default double[] getVertexCoord(int i) {
        return getVertexEdge(i).getStartCoord();
    }

    default void setVertexCoord(int i, double[] coord) {
        getVertexEdge(i).setStartCoord(coord);
    }

    default void setVertex(int i, Node node) {
        getVertexEdge(i).setStart(node);
    }

    default void forEachNeighbor(Consumer<? super MFCell> recorder) {
        getVertexEdge(0).iterator(MFEdge.class).forEachRemaining(edge -> {
            final MFEdge opposite = edge.getOpposite();
            if (null == opposite) {
                return;
            }
            recorder.accept(opposite.getCell());
        });
    }
}
