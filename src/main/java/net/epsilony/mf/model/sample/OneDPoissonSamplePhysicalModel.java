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

import java.util.Arrays;

import net.epsilony.mf.model.ChainPhysicalModel;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.model.load.ConstantNodeLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSamplePhysicalModel extends ChainPhysicalModel {

    public static final int START_COORD = 0, END_COORD = 1;
    public static final int VALUE_DIMENSIN = 1;

    public enum OneDPoissonSample {

    ZERO, CONSTANT, LINEAR, TRIGONOMETRIC;

    UnivariateFunction getVolumeLoad() {
        return volumeLoads[ordinal()];
    }

    public double[] getStartEndDirichlet() {
        return Arrays.copyOf(startEndDirichlets[ordinal()], 2);
    }

    public UnivariateFunction getSolution() {
        return solutions[ordinal()];
    }

    public static final UnivariateFunction[] volumeLoads = new UnivariateFunction[] { new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 0;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 8;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 8 + 16 * x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x);
        }
    } };
    public static final double[][] startEndDirichlets = new double[][] { { 0, 1 }, { 0, 1 }, { 0, 1 }, { 0, 0 } };
    public static final UnivariateFunction[] solutions = new UnivariateFunction[] { new UnivariateFunction() {
        @Override
        public double value(double x) {
            return x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return -4 * x * x + 5 * x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return -8.0 / 3 * x * x * x - 4 * x * x + 23.0 / 3 * x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return Math.sin(Math.PI * 2 * x);
        }
    } };
    }

    OneDPoissonSample choice;

    public OneDPoissonSamplePhysicalModel() {
        super();
        setTerminalPosition(true, START_COORD);
        setTerminalPosition(false, END_COORD);
        setValueDimension(VALUE_DIMENSIN);
    }

    public OneDPoissonSample getChoice() {
        return choice;
    }

    public void setChoice(OneDPoissonSample choice) {
        this.choice = choice;
        setLoadOnTerminalVertex(true, genStartEndLoad(true));
        setLoadOnTerminalVertex(false, genStartEndLoad(false));
        setVolumeLoad(genVolumeLoad());
    }

    private NodeLoad genStartEndLoad(boolean start) {
        double load = choice.getStartEndDirichlet()[start ? 0 : 1];
        ConstantNodeLoad result = new ConstantNodeLoad();
        result.setValue(new double[] { load });
        result.setValidity(new boolean[] { true });
        return result;
    }

    private SegmentLoad genVolumeLoad() {

        SegmentLoad result;
        result = new AbstractSegmentLoad() {
            UnivariateFunction volumeLoad = choice.getVolumeLoad();

            @Override
            public boolean isDirichlet() {
                return false;
            }

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[] { volumeLoad.value(coord[0]) };
            }

            @Override
            public boolean[] getValidity() {
                return null;
            }
        };
        return result;
    }

    @Override
    public void setValueDimension(int valueDimension) {
        if (valueDimension != VALUE_DIMENSIN) {
            throw new IllegalArgumentException();
        }
        super.setValueDimension(valueDimension);
    }

}
