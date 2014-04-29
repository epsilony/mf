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
public class SimpMFLine implements MFLine {
    private Node start;
    private MFLine pred, succ;

    public SimpMFLine() {
    }

    public SimpMFLine(Node start, MFLine pred, MFLine succ) {
        this.start = start;
        this.pred = pred;
        this.succ = succ;
    }

    public SimpMFLine(Node start) {
        this.start = start;
    }

    @Override
    public Node getStart() {
        return start;
    }

    @Override
    public void setStart(Node start) {
        this.start = start;
    }

    @Override
    public MFLine getPred() {
        return pred;
    }

    @Override
    public void setPred(MFLine pred) {
        this.pred = pred;
    }

    @Override
    public MFLine getSucc() {
        return succ;
    }

    @Override
    public void setSucc(MFLine succ) {
        this.succ = succ;
    }

    @Override
    public String toString() {
        return "SimpMFLine [start=" + start + "]";
    }

}
