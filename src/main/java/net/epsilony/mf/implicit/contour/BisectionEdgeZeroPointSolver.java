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
package net.epsilony.mf.implicit.contour;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.tb.analysis.Math2D;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class BisectionEdgeZeroPointSolver implements Function<MFEdge, double[]> {
    private ToDoubleFunction<double[]> levelFunction;
    private int maxEval;
    private double relativeAccuracy, absoluteAccuracy;
    private BisectionSolver solver;
    public static final int DEFAULT_MAX_EVAL;
    public static final double DEFAULT_RELATIVE_ACCURACY;
    public static final double DEFAULT_ABSOLUTE_ACCURARY;

    static {
        BisectionSolver solver = new BisectionSolver();
        DEFAULT_MAX_EVAL = 50;
        DEFAULT_RELATIVE_ACCURACY = solver.getRelativeAccuracy();
        DEFAULT_ABSOLUTE_ACCURARY = solver.getAbsoluteAccuracy();
    }

    private MFEdge edge;

    private final UnivariateFunction oneLineFunction = new UnivariateFunction() {
        private final double[] coord = new double[2];

        @Override
        public double value(double x) {
            Math2D.pointOnSegment(edge.getStartCoord(), edge.getEndCoord(), x, coord);
            return levelFunction.applyAsDouble(coord);
        }
    };

    @Override
    public double[] apply(MFEdge edge) {
        this.edge = edge;
        if (null == solver) {
            initSolver();
        }
        double t = solver.solve(maxEval > 0 ? maxEval : DEFAULT_MAX_EVAL, oneLineFunction, 0, 1, 0.5);
        return Math2D.pointOnSegment(edge.getStartCoord(), edge.getEndCoord(), t, null);
    }

    private void initSolver() {
        solver = new BisectionSolver(relativeAccuracy > 0 ? relativeAccuracy : DEFAULT_RELATIVE_ACCURACY,
                absoluteAccuracy > 0 ? absoluteAccuracy : DEFAULT_ABSOLUTE_ACCURARY);
    }

    public ToDoubleFunction<double[]> getLevelFunction() {
        return levelFunction;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        Objects.requireNonNull(levelFunction);
        this.levelFunction = levelFunction;
    }

    public int getMaxEval() {
        return maxEval;
    }

    public void setMaxEval(int maxEval) {
        this.maxEval = maxEval;
    }

    public double getRelativeAccuracy() {
        return relativeAccuracy;
    }

    public void setRelativeAccuracy(double relativeAccuracy) {
        this.relativeAccuracy = relativeAccuracy;
        solver = null;
    }

    public double getAbsoluteAccuracy() {
        return absoluteAccuracy;
    }

    public void setAbsoluteAccuracy(double absoluteAccuracy) {
        this.absoluteAccuracy = absoluteAccuracy;
        solver = null;
    }

}
