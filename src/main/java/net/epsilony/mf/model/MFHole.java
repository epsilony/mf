package net.epsilony.mf.model;

import java.util.function.ToDoubleFunction;

import net.epsilony.mf.implicit.dist_func.CircleDstFunction;

public class MFHole {
    private double[] center;
    private double radius;

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public MFHole(double[] center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public MFHole() {
    }

    public ToDoubleFunction<double[]> distanceFunction() {
        return new CircleDstFunction(center, radius);
    }
}
