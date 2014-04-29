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
package net.epsilony.mf.adapt;

import java.util.List;

import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpFissionResult implements FissionResult {

    private List<MFCell> newCells;
    private List<Node> newNodes;
    private MFCell fissioned;

    public SimpFissionResult() {
    }

    public SimpFissionResult(List<MFCell> newCells, List<Node> newNodes, MFCell fissioned) {
        this.newCells = newCells;
        this.newNodes = newNodes;
        this.fissioned = fissioned;
    }

    @Override
    public List<MFCell> getNewCells() {
        return newCells;
    }

    public void setNewCells(List<MFCell> newCells) {
        this.newCells = newCells;
    }

    @Override
    public List<Node> getNewNodes() {
        return newNodes;
    }

    public void setNewNodes(List<Node> newNodes) {
        this.newNodes = newNodes;
    }

    @Override
    public MFCell getFissioned() {
        return fissioned;
    }

    public void setFissioned(MFCell fissioned) {
        this.fissioned = fissioned;
    }

}
