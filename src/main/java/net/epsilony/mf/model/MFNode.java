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

import java.util.Collection;

import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.util.persist.Record;
import net.epsilony.tb.solid.Node;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNode extends Node implements MFGeomUnit {

    public static double calcMaxInfluenceRadius(Collection<? extends MFNode> nodes) {
        double maxRadius = 0;
        for (MFNode node : nodes) {
            final double influenceRadius = node.getInfluenceRadius();
            if (maxRadius < influenceRadius) {
                maxRadius = influenceRadius;
            }
        }
        return maxRadius;
    }

    public MFNode(double[] coord, boolean copy) {
        super(coord, copy);
    }

    public MFNode(double[] coord) {
        super(coord);
    }

    public MFNode(double x, double y) {
        super(x, y);
    }

    public MFNode() {
    }

    @Record
    double    influenceRadius;
    @Record
    int       assemblyIndex         = -1;
    @Record
    int       lagrangeAssemblyIndex = -1;
    @Record
    double[]  value;
    @Record
    double[]  lagrangeValue;
    @Record
    boolean[] lagrangeValueValidity;
    MFLine    asStart;

    public double getInfluenceRadius() {
        return influenceRadius;
    }

    public void setInfluenceRadius(double influenceRadius) {
        this.influenceRadius = influenceRadius;
    }

    public int getAssemblyIndex() {
        return assemblyIndex;
    }

    public void setAssemblyIndex(int assemblyIndex) {
        this.assemblyIndex = assemblyIndex;
    }

    public int getLagrangeAssemblyIndex() {
        return lagrangeAssemblyIndex;
    }

    public void setLagrangeAssemblyIndex(int lagrangeAssemblyIndex) {
        this.lagrangeAssemblyIndex = lagrangeAssemblyIndex;
    }

    public boolean[] getLagrangeValueValidity() {
        return lagrangeValueValidity;
    }

    public void setLagrangeValueValidity(boolean[] lagrangeValueValidity) {
        this.lagrangeValueValidity = lagrangeValueValidity;
    }

    public double[] getValue() {
        return value;
    }

    public void setValue(double[] value) {
        this.value = value;
    }

    public double[] getLagrangeValue() {
        return lagrangeValue;
    }

    public void setLagrangeValue(double[] lagrangeValue) {
        this.lagrangeValue = lagrangeValue;
    }
}
