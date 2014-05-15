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
package net.epsilony.mf.opt;

import java.util.List;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.opt.sample.RangeBarrier;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LevelOptModel {
    private List<MFCell> cells;
    private List<MFNode> levelFunctionNodes;
    private RangeBarrier rangeBarrier;
    private ToDoubleFunction<double[]> startLevelFunction;

    public LevelOptModel(List<MFCell> cells, List<MFNode> levelFunctionNodes, RangeBarrier rangeBarrier,
            ToDoubleFunction<double[]> startLevelFunction) {
        this.cells = cells;
        this.levelFunctionNodes = levelFunctionNodes;
        this.rangeBarrier = rangeBarrier;
        this.startLevelFunction = startLevelFunction;
    }

    public LevelOptModel() {
    }

    public List<MFCell> getCells() {
        return cells;
    }

    public void setCells(List<MFCell> cells) {
        this.cells = cells;
    }

    public List<MFNode> getLevelFunctionNodes() {
        return levelFunctionNodes;
    }

    public void setLevelFunctionNodes(List<MFNode> levelFunctionNodes) {
        this.levelFunctionNodes = levelFunctionNodes;
    }

    public RangeBarrier getRangeBarrier() {
        return rangeBarrier;
    }

    public void setRangeBarrier(RangeBarrier rangeBarrier) {
        this.rangeBarrier = rangeBarrier;
    }

    public ToDoubleFunction<double[]> getStartLevelFunction() {
        return startLevelFunction;
    }

    public void setStartLevelFunction(ToDoubleFunction<double[]> startLevelFunction) {
        this.startLevelFunction = startLevelFunction;
    }

}
