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
package net.epsilony.mf.integrate.integrator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.SimpGeomPoint;
import net.epsilony.mf.integrate.unit.SimpGeomQuadraturePoint;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LineToGeomQuadraturePoints implements Function<MFLine, List<GeomQuadraturePoint>> {
    protected final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();
    protected boolean geomAsBoundary = true;
    protected Object oneOffOverrideLoadKey = null;

    public int getQuadratuePointsNum() {
        return linearQuadratureSupport.getQuadratuePointsNum();
    }

    public int getQuadratureDegree() {
        return linearQuadratureSupport.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        linearQuadratureSupport.setQuadratureDegree(quadratureDegree);
    }

    public boolean isGeomAsBoundary() {
        return geomAsBoundary;
    }

    public void setGeomAsBoundary(boolean geomAsBoundary) {
        this.geomAsBoundary = geomAsBoundary;
    }

    public Object getOneOffOverrideLoadKey() {
        return oneOffOverrideLoadKey;
    }

    public void setOneOffOverrideLoadKey(Object overrideLoadKey) {
        this.oneOffOverrideLoadKey = overrideLoadKey;
    }

    @Override
    public List<GeomQuadraturePoint> apply(MFLine line) {
        List<GeomQuadraturePoint> result = new ArrayList<>(linearQuadratureSupport.getQuadratuePointsNum());
        linearQuadratureSupport.setStartEndCoords(line.getStartCoord(), line.getEndCoord());
        linearQuadratureSupport.reset();
        while (linearQuadratureSupport.hasNext()) {
            linearQuadratureSupport.next();
            SimpGeomQuadraturePoint gqp = new SimpGeomQuadraturePoint();
            SimpGeomPoint geomPoint = new SimpGeomPoint();
            geomPoint.setCoord(linearQuadratureSupport.getLinearCoord());
            geomPoint.setGeomCoord(linearQuadratureSupport.getLinearParameter());
            geomPoint.setGeomUnit(line);
            geomPoint.setLoadKey(oneOffOverrideLoadKey == null ? line : oneOffOverrideLoadKey);
            geomPoint.setGeomAsBoundary(geomAsBoundary);
            gqp.setGeomPoint(geomPoint);
            gqp.setWeight(linearQuadratureSupport.getLinearWeight());
            result.add(gqp);
        }
        clearOneOffOverrides();
        return result;
    }

    private void clearOneOffOverrides() {
        oneOffOverrideLoadKey = null;
    }

}
