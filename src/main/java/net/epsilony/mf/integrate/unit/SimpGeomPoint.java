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

import java.util.Arrays;

import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class SimpGeomPoint implements GeomPoint {
    protected GeomUnit geomUnit;
    protected double[] geomCoord;
    protected double[] coord;
    protected Object loadKey;

    public SimpGeomPoint() {
    }

    public SimpGeomPoint(GeomPoint geomPoint) {
        this.geomUnit = geomPoint.getGeomUnit();
        this.loadKey = geomPoint.getLoadKey();
        double[] geomCoord = geomPoint.getGeomCoord();
        if (null != geomCoord) {
            this.geomCoord = Arrays.copyOf(geomCoord, geomCoord.length);
        }
        double[] coord = geomPoint.getCoord();
        if (null != coord) {
            this.coord = Arrays.copyOf(coord, coord.length);
        }
    }

    public SimpGeomPoint(GeomUnit geomUnit, double[] geomCoord, double[] coord, Object loadKey) {
        this.geomUnit = geomUnit;
        this.geomCoord = geomCoord;
        this.coord = coord;
        this.loadKey = loadKey;
    }

    @Override
    public Object getLoadKey() {
        return loadKey;
    }

    public void setLoadKey(Object loadKey) {
        this.loadKey = loadKey;
    }

    @Override
    public GeomUnit getGeomUnit() {
        return geomUnit;
    }

    public void setGeomUnit(GeomUnit geomUnit) {
        this.geomUnit = geomUnit;
    }

    @Override
    public double[] getGeomCoord() {
        return geomCoord;
    }

    public void setGeomCoord(double... geomCoord) {
        this.geomCoord = geomCoord;
    }

    @Override
    public double[] getCoord() {
        return coord;
    }

    public void setCoord(double... coord) {
        this.coord = coord;
    }

}
