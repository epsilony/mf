/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.Collection;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNode extends Node{

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
    double influenceRadius;
    int assemblyIndex = -1;
    int lagrangeAssemblyIndex = -1;
    double[] value;
    double[] lagrangeValue;
    boolean[] lagrangeValueValidity;
    Segment asStart;

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
