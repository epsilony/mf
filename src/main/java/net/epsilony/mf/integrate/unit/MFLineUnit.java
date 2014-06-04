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
package net.epsilony.mf.integrate.unit;

import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFLineUnit {
    private boolean asBoundary = false;
    private MFLine  line;
    private Object  overrideLoadKey;

    public MFLineUnit(boolean asBoundary, MFLine line, Object loadKey) {
        this.asBoundary = asBoundary;
        this.line = line;
        this.overrideLoadKey = loadKey;
    }

    public MFLineUnit() {
    }

    public boolean isAsBoundary() {
        return asBoundary;
    }

    public void setAsBoundary(boolean asBoundary) {
        this.asBoundary = asBoundary;
    }

    public MFLine getLine() {
        return line;
    }

    public void setLine(MFLine line) {
        this.line = line;
    }

    public Object getOverrideLoadKey() {
        return overrideLoadKey;
    }

    public void setOverrideLoadKey(Object loadKey) {
        this.overrideLoadKey = loadKey;
    }

}
