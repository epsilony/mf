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
package net.epsilony.mf.opt.integrate;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.tuple.TwoTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ObjectIntegralCalculator {

    private LevelFunctionalIntegrator                                                                    integrator;
    private LevelFunctionalIntegralUnitsGroup                                                            integralUnitsGroup;
    private Function<double[], ? extends TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue>> levelPackFunction;
    private Function<Object, Stream<GeomQuadraturePoint>>                                                commonUnitToPoints;
    private List<GeomQuadraturePoint>                                                                    volumeIntegralPoints;
    private List<GeomQuadraturePoint>                                                                    boundaryIntegralPoints;

    public ObjectIntegralCalculator(
            LevelFunctionalIntegrator integrator,
            LevelFunctionalIntegralUnitsGroup integralUnitsGroup,
            Function<double[], ? extends TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue>> levelPackFunction,
            Function<Object, Stream<GeomQuadraturePoint>> commonUnitToPoints) {
        this.integrator = integrator;
        this.integralUnitsGroup = integralUnitsGroup;
        this.levelPackFunction = levelPackFunction;
        this.commonUnitToPoints = commonUnitToPoints;
    }

    public ObjectIntegralCalculator() {
    }

    public void setIntegrator(LevelFunctionalIntegrator integrator) {
        this.integrator = integrator;
    }

    public void setLevelPackFunction(
            Function<double[], ? extends TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue>> levelPackFunction) {
        this.levelPackFunction = levelPackFunction;
    }

    public void setCommonUnitToPoints(Function<Object, Stream<GeomQuadraturePoint>> commonUnitToPoints) {
        this.commonUnitToPoints = commonUnitToPoints;
    }

    public void calculate() {
        boundaryIntegralPoints.forEach(this::boundaryIntegrate);
        volumeIntegralPoints.forEach(this::volumeIntegrate);
    }

    public void calculatePrepare() {
        integrator.prepare();
        integralUnitsGroup.prepare();
        volumeIntegralPoints = integralUnitsGroup.volume().flatMap(commonUnitToPoints).collect(Collectors.toList());
        boundaryIntegralPoints = integralUnitsGroup.boundary().flatMap(commonUnitToPoints).collect(Collectors.toList());
    }

    private void boundaryIntegrate(GeomQuadraturePoint gqp) {
        TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue> pack = levelPackFunction.apply(gqp
                .getGeomPoint().getCoord());
        integrator.boundaryIntegrate(gqp, pack.getFirst(), pack.getSecond());
    }

    private void volumeIntegrate(GeomQuadraturePoint gqp) {
        integrator.volumeIntegrate(gqp, null, null);
    }

    public double value() {
        return integrator.value();
    }

    public double[] gradient() {
        return integrator.gradient();
    }

    public LevelFunctionalIntegrator getIntegrator() {
        return integrator;
    }

    public void setIntegralUnitsGroup(LevelFunctionalIntegralUnitsGroup integralUnitsGroup) {
        this.integralUnitsGroup = integralUnitsGroup;
    }

    public void setGradientSize(int gradientSize) {
        integrator.setGradientSize(gradientSize);
    }

    public List<GeomQuadraturePoint> getVolumeIntegralPoints() {
        return volumeIntegralPoints;
    }

    public List<GeomQuadraturePoint> getBoundaryIntegralPoints() {
        return boundaryIntegralPoints;
    }

}
