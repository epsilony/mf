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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFFacetFactory;
import net.epsilony.mf.model.geom.util.TriangleGridFactory;
import net.epsilony.mf.model.geom.util.TriangleGridFactory.TriangleGrid;
import net.epsilony.mf.opt.LevelOptModel;
import net.epsilony.mf.opt.sample.RangeBarrier.AllAtTriangleEdgeMidPointsFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RangeMarginLevelOptModelFactory {
    private double left, up;
    private int width;
    private int height;
    private int widthMargin;
    private int heightMargin;
    private double triangleWidth, triangleHeight;

    private Predicate<MFLine> fixRangeLinePredicate;

    public RangeMarginLevelOptModelFactory(double left, double up, int width, int height, int widthMargin,
            int heightMargin, double triangleWidth, double triangleHeight, Predicate<MFLine> fixRangeLinePredicate) {
        this.left = left;
        this.up = up;
        this.width = width;
        this.height = height;
        this.widthMargin = widthMargin;
        this.heightMargin = heightMargin;
        this.triangleWidth = triangleWidth;
        this.triangleHeight = triangleHeight;
        this.fixRangeLinePredicate = fixRangeLinePredicate;
    }

    public RangeMarginLevelOptModelFactory() {
    }

    public LevelOptModel produce() {
        LevelOptModel model = new LevelOptModel();
        TriangleGrid triangleGrid = genTriangleGrids();
        RangeBarrier rangeBarrier = genRectangleBarrier(triangleGrid);

        model.setCells(Arrays.stream(triangleGrid.getTriangles()).flatMap(Arrays::stream).collect(Collectors.toList()));
        model.setRangeBarrier(rangeBarrier);
        model.setStartLevelFunction(genRangeLevelFunction());
        List<MFNode> spaceNodes = Arrays.stream(triangleGrid.getVertesGrid()).flatMap(Arrays::stream)
                .map(nd -> (MFNode) nd).collect(Collectors.toList());
        Set<MFNode> rangeBarrierNodes = rangeBarrier.getAll().stream().map(line -> (MFNode) line.getStart())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<MFNode> allNodes = new ArrayList<>(spaceNodes);
        allNodes.addAll(rangeBarrierNodes);
        model.setLevelFunctionNodes(allNodes);

        return model;
    }

    private ToDoubleFunction<double[]> genRangeLevelFunction() {
        final MFFacet facet = new MFFacetFactory(SimpMFLine::new, MFNode::new).produceBySingleChain(genRange()
                .vertesCoords());
        ToDoubleFunction<double[]> initLevelFunction = facet.asDistanceFunction();
        return initLevelFunction;
    }

    private MFRectangle genRange() {
        double right = left + width * triangleWidth;
        double down = up - height * triangleHeight;
        return new MFRectangle(down, right, up, left);
    }

    private TriangleGrid genTriangleGrids() {
        TriangleGridFactory factory = TriangleGridFactory.commonInstance();
        factory.setNumRowsCols(height + 1 + 2 * heightMargin, width * 2 + 2 + 2 * widthMargin);
        factory.setTriangleWidthHeight(triangleWidth, triangleHeight);
        factory.setUpLeft(up + (0.5 + heightMargin) * triangleHeight, left - (widthMargin * 0.5 + 0.75) * triangleWidth);

        return factory.get();
    }

    private RangeBarrier genRectangleBarrier(TriangleGrid triangleGrid) {
        AllAtTriangleEdgeMidPointsFactory factory = new RangeBarrier.AllAtTriangleEdgeMidPointsFactory();
        factory.setFixedPredicate(fixRangeLinePredicate);
        factory.setTriangleColMargin(widthMargin);
        factory.setTriangleRowMargin(heightMargin);
        return factory.apply(triangleGrid);
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getUp() {
        return up;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidthMargin() {
        return widthMargin;
    }

    public void setWidthMargin(int widthMargin) {
        this.widthMargin = widthMargin;
    }

    public int getHeightMargin() {
        return heightMargin;
    }

    public void setHeightMargin(int heightMargin) {
        this.heightMargin = heightMargin;
    }

    public double getTriangleWidth() {
        return triangleWidth;
    }

    public void setTriangleWidth(double triangleWidth) {
        this.triangleWidth = triangleWidth;
    }

    public double getTriangleHeight() {
        return triangleHeight;
    }

    public void setTriangleHeight(double triangleHeight) {
        this.triangleHeight = triangleHeight;
    }

    public void setFixRangeLinePredicate(Predicate<MFLine> fixRangeLinePredicate) {
        this.fixRangeLinePredicate = fixRangeLinePredicate;
    }
}
