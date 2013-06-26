/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.Collection;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNode extends Node {

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
    double[] lagrangleValue;
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

    public double[] getValue() {
        return value;
    }

    public void setValue(double[] value) {
        this.value = value;
    }

    public double[] getLagrangleValue() {
        return lagrangleValue;
    }

    public void setLagrangleValue(double[] lagrangleValue) {
        this.lagrangleValue = lagrangleValue;
    }

    public Segment getAsStart() {
        return asStart;
    }

    public void setAsStart(Segment asStart) {
        this.asStart = asStart;
    }
}
