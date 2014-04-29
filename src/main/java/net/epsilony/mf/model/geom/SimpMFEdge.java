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
package net.epsilony.mf.model.geom;

import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpMFEdge extends SimpMFLine implements MFEdge {
    private MFCell cell;
    private MFEdge opposite;

    public SimpMFEdge() {
    }

    public SimpMFEdge(Node start) {
        super(start);
    }

    @Override
    public MFCell getCell() {
        return cell;
    }

    @Override
    public void setCell(MFCell cell) {
        this.cell = cell;
    }

    @Override
    public MFEdge getOpposite() {
        return opposite;
    }

    @Override
    public void setOpposite(MFEdge opposite) {
        this.opposite = opposite;
    }
}
