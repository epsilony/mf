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
package net.epsilony.mf.opt.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFLineChainFactory;
import net.epsilony.mf.model.geom.util.TriangleGridFactory.TriangleGrid;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RangeBarrier implements Serializable {
    private List<MFLine> unfixed;
    private List<MFLine> fixed;
    private List<MFLine> all;

    public List<MFLine> getUnfixed() {
        return unfixed;
    }

    public void setUnfixed(List<MFLine> unfixed) {
        this.unfixed = unfixed;
    }

    public List<MFLine> getFixed() {
        return fixed;
    }

    public void setFixed(List<MFLine> fixed) {
        this.fixed = fixed;
    }

    public List<MFLine> getAll() {
        return all;
    }

    public void setAll(List<MFLine> all) {
        this.all = all;
    }

    public static class AllAtTriangleEdgeMidPointsFactory implements Function<TriangleGrid, RangeBarrier> {

        private int triangleRowMargin;
        private int triangleColMargin;
        private Predicate<MFLine> fixedPredicate;

        @Override
        public RangeBarrier apply(TriangleGrid triangleGrid) {
            // if without triangle margins:
            // * still works
            // * {down right}/{left up} vertex is the mid point of 3rd edge of
            // {down right}/{left up} triangle the
            // * triangle cols must be even and >=4;
            // * triangle rows must be >=2;

            if (triangleGrid.getTriangleColsNum() % 2 != 0) {
                throw new IllegalArgumentException();
            }
            if (triangleGrid.getTriangleRowsNum() < 4) {
                throw new IllegalArgumentException();
            }
            double left = triangleGrid.getLeft() + (0.75 + triangleColMargin / 2.0) * triangleGrid.getTriangleWidth();
            double up = triangleGrid.getUp() - (0.5 + triangleRowMargin) * triangleGrid.getTriangleHeight();
            Node[][] triVertesGrid = triangleGrid.getVertesGrid();
            double[] triRightDown = triVertesGrid[triVertesGrid.length - 1][triVertesGrid[triVertesGrid.length - 1].length - 1]
                    .getCoord();
            double[] triRightUp = triVertesGrid[0][triVertesGrid[0].length - 1].getCoord();
            double right = triRightUp[0] - (0.75 + triangleColMargin / 2.0) * triangleGrid.getTriangleWidth();
            double down = triRightDown[1] + (0.5 + triangleRowMargin);
            if (left >= right) {
                throw new IllegalArgumentException();
            }
            if (down >= up) {
                throw new IllegalArgumentException();
            }
            double[][] dxys = { { 0, -triangleGrid.getTriangleHeight() }, { triangleGrid.getTriangleWidth(), 0 },
                    { 0, triangleGrid.getTriangleHeight() }, { -triangleGrid.getTriangleWidth(), 0 } };
            int verNum = triangleGrid.getTriangleRowsNum() - 1 - 2 * triangleRowMargin;
            int horiNum = (triangleGrid.getTriangleColsNum() - 2 * triangleColMargin) / 2 - 1;
            int[] sideNums = { verNum, horiNum, verNum, horiNum };
            double[][] starts = { { left, up }, { left, down }, { right, down }, { right, up } };

            ArrayList<double[]> coords = new ArrayList<>((horiNum + verNum) * 2);

            for (int i = 0; i < 4; i++) {
                int num = sideNums[i];
                double[] dxy = dxys[i];
                double dx = dxy[0];
                double dy = dxy[1];
                double[] start = starts[i];
                coords.add(start);
                for (int j = 1; j < num; j++) {
                    coords.add(new double[] { start[0] + dx * j, start[1] + dy * j });
                }
            }

            MFLineChainFactory chainFactory = new MFLineChainFactory(SimpMFLine::new, MFNode::new);
            chainFactory.setClosed(true);
            MFLine chainHead = chainFactory.produce(coords);
            List<MFLine> allLines = chainHead.stream().collect(Collectors.toList());

            List<MFLine> fixed = allLines.stream().filter(fixedPredicate).collect(Collectors.toList());
            List<MFLine> unFixed = allLines.stream().filter(fixedPredicate.negate()).collect(Collectors.toList());

            RangeBarrier result = new RangeBarrier();
            result.setAll(allLines);
            result.setFixed(fixed);
            result.setUnfixed(unFixed);
            return result;
        }

        public int getTriangleRowMargin() {
            return triangleRowMargin;
        }

        public void setTriangleRowMargin(int triangleRowMargin) {
            this.triangleRowMargin = triangleRowMargin;
        }

        public int getTriangleColMargin() {
            return triangleColMargin;
        }

        public void setTriangleColMargin(int triangleColMargin) {
            this.triangleColMargin = triangleColMargin;
        }

        public void setFixedPredicate(Predicate<MFLine> fixedPredicate) {
            this.fixedPredicate = fixedPredicate;
        }

    }
}
