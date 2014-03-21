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
package net.epsilony.mf.model.convertor;

import java.util.function.Function;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class CoordToNode<N extends Node> implements Function<double[], N> {
    RudeFactory<N> nodeFactory;

    @Override
    public N apply(double[] input) {
        N node = nodeFactory.produce();
        node.setCoord(input);
        return node;
    }

    public CoordToNode() {
    }

    public CoordToNode(Class<N> nodeClass) {
        setNodeClass(nodeClass);
    }

    public Class<? extends N> getNodeClass() {
        return nodeFactory.getObjectClass();
    }

    public void setNodeClass(Class<N> objectClass) {
        nodeFactory.setObjectClass(objectClass);
    }

}
