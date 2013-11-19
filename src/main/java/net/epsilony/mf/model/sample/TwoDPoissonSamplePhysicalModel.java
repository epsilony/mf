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
package net.epsilony.mf.model.sample;

import static net.epsilony.mf.model.MFRectangleEdge.DOWN;
import static net.epsilony.mf.model.MFRectangleEdge.LEFT;
import static net.epsilony.mf.model.MFRectangleEdge.RIGHT;
import static net.epsilony.mf.model.MFRectangleEdge.UP;
import static org.apache.commons.math3.util.FastMath.PI;
import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.cos;
import static org.apache.commons.math3.util.FastMath.sin;
import net.epsilony.mf.model.RectanglePhysicalModel;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.model.load.AbstractSpatialLoad;
import net.epsilony.mf.model.load.SpatialLoad;
import net.epsilony.tb.analysis.ArrvarFunction;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TwoDPoissonSamplePhysicalModel extends RectanglePhysicalModel {
    public static final int VALUE_DIMENSION = 1;

    private interface NeumannBoundaryFunction {

        double value(double[] coord, double[] unitOutNormal);
    }

    public enum TwoDPoissonSample {

    LINEAR, QUADRATIC, SIN;

    public double getSolution(double[] coord) {
        return SOLUTIONS[ordinal()].value(coord);
    }

    public double getVolumeSource(double[] coord) {
        return VOLUME_SOURCES[ordinal()].value(coord);
    }

    public double getNeumannBoundaryCondition(double[] coord, double[] unitOutNormal) {
        final double UNITY_TOL = 1e-12;
        if (abs(Math2D.dot(unitOutNormal, unitOutNormal) - 1) > UNITY_TOL) {
            throw new IllegalArgumentException();
        }
        return NEUMANN_BCS[ordinal()].value(coord, unitOutNormal);
    }

    private static final ArrvarFunction[] SOLUTIONS = new ArrvarFunction[] { new ArrvarFunction() {
        @Override
        public double value(double[] vec) {
            double x = vec[0];
            double y = vec[1];
            return x + 2 * y;
        }
    }, new ArrvarFunction() {
        @Override
        public double value(double[] vec) {
            double x = vec[0];
            double y = vec[1];
            return 0.1 * x + 0.3 * y + 0.8 * x * x + 1.2 * x * y + 0.6 * y * y;
        }
    }, new ArrvarFunction() {
        @Override
        public double value(double[] vec) {
            double x = vec[0];
            double y = vec[1];
            return sin(PI * x) * sin(PI * y) / (2 * PI * PI);
        }
    } };
    private static final ArrvarFunction[] VOLUME_SOURCES = new ArrvarFunction[] { new ArrvarFunction() {
        @Override
        public double value(double[] vec) {
            return 0;
        }
    }, new ArrvarFunction() {
        @Override
        public double value(double[] vec) {
            return -2.8;
        }
    }, new ArrvarFunction() {
        @Override
        public double value(double[] vec) {
            double x = vec[0];
            double y = vec[1];
            return sin(PI * x) * sin(PI * y);
        }
    } };
    private static final NeumannBoundaryFunction[] NEUMANN_BCS = new NeumannBoundaryFunction[] {
            new NeumannBoundaryFunction() {
                @Override
                public double value(double[] coord, double[] unitOutNormal) {
                    return unitOutNormal[0] + 2 * unitOutNormal[1];
                }
            }, new NeumannBoundaryFunction() {
                @Override
                public double value(double[] coord, double[] unitOutNormal) {
                    double x = coord[0];
                    double y = coord[1];
                    double dx = 0.1 + 1.6 * x + 1.2 * y;
                    double dy = 0.3 * 1.2 * x + 1.2 * y;
                    return dx * unitOutNormal[0] + dy * unitOutNormal[1];
                }
            }, new NeumannBoundaryFunction() {
                @Override
                public double value(double[] coord, double[] unitOutNormal) {
                    double x = coord[0];
                    double y = coord[1];
                    double dx = cos(PI * x) * sin(PI * y) / (2 * PI);
                    double dy = sin(PI * x) * cos(PI * y) / (2 * PI);
                    return unitOutNormal[0] * dx + unitOutNormal[1] * dy;
                }
            } };
    };

    TwoDPoissonSample sampleCase;

    public void setTwoDPoissonSample(TwoDPoissonSample twoDPoissonSample) {
        this.sampleCase = twoDPoissonSample;
        setValueDimension(VALUE_DIMENSION);
        setupRetangleSize();
        setupBoundaryConditions();
    }

    private void setupRetangleSize() {
        super.setEdgePosition(DOWN, -1);
        super.setEdgePosition(LEFT, -1);
        super.setEdgePosition(UP, 1);
        super.setEdgePosition(RIGHT, 1);
    }

    private void setupBoundaryConditions() {
        AbstractSegmentLoad upLoad = new AbstractSegmentLoad() {
            @Override
            public boolean isDirichlet() {
                return true;
            }

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[] { sampleCase.getSolution(coord) };
            }

            @Override
            public boolean[] getValidity() {
                return new boolean[] { true };
            }
        };

        AbstractSegmentLoad neumannLoad = new AbstractSegmentLoad() {
            @Override
            public double[] getValue() {
                double[] coord = segment.values(parameter, null);
                double[] unitOutNormal = Segment2DUtils.chordUnitOutNormal(segment, null);
                return new double[] { sampleCase.getNeumannBoundaryCondition(coord, unitOutNormal) };
            }
        };

        SpatialLoad volumeLoad = new AbstractSpatialLoad() {

            @Override
            public double[] getValue() {
                double value = sampleCase.getVolumeSource(coord);
                return new double[] { value };
            }
        };

        setEdgeLoad(UP, upLoad);
        setEdgeLoad(DOWN, neumannLoad);
        setEdgeLoad(LEFT, neumannLoad);
        setEdgeLoad(RIGHT, neumannLoad);
        setVolumeLoad(volumeLoad);
    }
}
