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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.function.Function;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Line;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class SingleLineFractionizer implements Function<Line, List<double[]>> {
    Random random = new Random();
    double disturbRatio = 0;

    @Override
    /**
     * @return new coordinates from line start (exclusive) to line end (exclusive)
     */
    public List<double[]> apply(Line line) {
        List<double[]> coords = genUnitDistributedCoords(line);
        return disturbCoords(line, coords);
    }

    private List<double[]> disturbCoords(Line line, List<double[]> coords) {
        if (0 == disturbRatio) {
            return coords;
        }
        ArrayList<double[]> coordsBack = Lists.newArrayList(coords);
        coords = new ArrayList<>(coordsBack.size());
        for (int i = 0; i < coordsBack.size(); i++) {
            double[] pre = i == 0 ? line.getStartCoord() : coordsBack.get(i - 1);
            double[] next = i == coordsBack.size() - 1 ? line.getEndCoord() : coordsBack.get(i + 1);
            double[] coord = coordsBack.get(i);
            coords.add(genDisturbedCoord(pre, coord, next));
        }
        return coords;
    }

    private double[] genDisturbedCoord(double[] pre, double[] coord, double[] next) {
        double[] result = new double[coord.length];
        for (int i = 0; i < result.length; i++) {
            double rangeT = disturbRatio * 0.5;
            double rangeStart = coord[i] * (1 - rangeT) + pre[i] * rangeT;
            double rangeEnd = coord[i] * (1 - rangeT) + next[i] * rangeT;
            double rand = 0;

            rand = random.nextDouble();

            result[i] = rangeStart * (1 - rand) + rangeEnd * rand;
        }
        return result;
    }

    abstract protected List<double[]> genUnitDistributedCoords(Line line);

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public double getDisturbRatio() {
        return disturbRatio;
    }

    public void setDisturbRatio(double disturbRatio) {
        if (disturbRatio < 0 || disturbRatio >= 1) {
            throw new IllegalArgumentException();
        }
        this.disturbRatio = disturbRatio;
    }

    public static class ByNumberOfNewCoords extends SingleLineFractionizer {
        int numOfNewCoords;

        public int getNumOfNewCoords() {
            return numOfNewCoords;
        }

        public void setNumOfNewCoords(int numOfNewCoords) {
            if (numOfNewCoords < 1) {
                throw new IllegalArgumentException();
            }
            this.numOfNewCoords = numOfNewCoords;
        }

        @Override
        protected List<double[]> genUnitDistributedCoords(Line line) {
            List<double[]> results = new ArrayList<>(numOfNewCoords);
            for (int i = 1; i <= numOfNewCoords; i++) {
                double t = 1.0 / (numOfNewCoords + 1) * i;
                double[] newCoord = Math2D.pointOnSegment(line.getStartCoord(), line.getEndCoord(), t, null);
                results.add(newCoord);
            }
            return results;
        }

        public ByNumberOfNewCoords(int numOfNewCoords) {
            setNumOfNewCoords(numOfNewCoords);
        }

        public ByNumberOfNewCoords() {
        }

    }

    public static class ByUndisturbedNeighbourCoordsDistanceSup extends SingleLineFractionizer {
        ByNumberOfNewCoords byNumberOfNewCoords = new ByNumberOfNewCoords();
        double undisturbedCoordsDistanceSup;

        public double getUndisturbedCoordsDistanceSup() {
            return undisturbedCoordsDistanceSup;
        }

        public void setUndisturbedCoordsDistanceSup(double undisturbedCoordsDistanceSup) {
            if (undisturbedCoordsDistanceSup <= 0) {
                throw new IllegalArgumentException();
            }
            this.undisturbedCoordsDistanceSup = undisturbedCoordsDistanceSup;
        }

        @Override
        protected List<double[]> genUnitDistributedCoords(Line line) {
            double dNum = Math.ceil(line.length() / undisturbedCoordsDistanceSup);
            if (dNum > Integer.MAX_VALUE) {
                throw new IllegalStateException();
            }
            byNumberOfNewCoords.setNumOfNewCoords((int) dNum - 1);
            return byNumberOfNewCoords.genUnitDistributedCoords(line);
        }

        public ByUndisturbedNeighbourCoordsDistanceSup(double lineLengthSup) {
            setUndisturbedCoordsDistanceSup(lineLengthSup);
        }

        public ByUndisturbedNeighbourCoordsDistanceSup() {
        }

    }
}
