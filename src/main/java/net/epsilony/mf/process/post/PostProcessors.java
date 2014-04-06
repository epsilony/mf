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
package net.epsilony.mf.process.post;

import java.util.ArrayList;
import java.util.Collection;

import net.epsilony.mf.model.MFNode;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PostProcessors {
    public static double[] collectSingleArrayNodesValues(Collection<? extends MFNode> nodes, int valueDimension) {
        double[] result = new double[nodes.size()];
        nodes.forEach((nd) -> {
            System.arraycopy(nd.getValue(), 0, result, valueDimension * nd.getAssemblyIndex(), valueDimension);
        });
        return result;
    }

    public static double[][] collectMultiArrayNodesValues(Collection<? extends MFNode> nodes) {
        double[][] result = new double[nodes.size()][];
        for (MFNode node : nodes) {
            result[node.getAssemblyIndex()] = node.getValue();
        }
        return result;
    }

    public static ArrayList<double[]> collectArrayListArrayNodesValues(Collection<? extends MFNode> nodes) {
        return Lists.newArrayList(collectMultiArrayNodesValues(nodes));
    }
}
