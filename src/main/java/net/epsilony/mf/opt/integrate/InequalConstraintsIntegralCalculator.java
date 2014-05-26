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

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.tuple.TwoTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class InequalConstraintsIntegralCalculator {
    private ArrayList<LevelFunctionalIntegrator> rangeIntegrators;
    private ArrayList<LevelFunctionalIntegrator> domainIntegrators;
    private LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup;
    private LevelFunctionalIntegralUnitsGroup domainIntegralUnitsGroup;
    private Function<double[], ? extends TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue>> levelPackFunction;
    private Function<Object, Stream<GeomQuadraturePoint>> commonUnitToPoints;

    public InequalConstraintsIntegralCalculator() {
    }

    public void setLevelPackFunction(
            Function<double[], ? extends TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue>> levelPackFunction) {
        this.levelPackFunction = levelPackFunction;
    }

    public void setCommonUnitToPoints(Function<Object, Stream<GeomQuadraturePoint>> commonUnitToPoints) {
        this.commonUnitToPoints = commonUnitToPoints;
    }

    public void setDomainIntegralUnitsGroup(LevelFunctionalIntegralUnitsGroup integralGroupSupplier) {
        this.domainIntegralUnitsGroup = integralGroupSupplier;
    }

    public void setRangeIntegralUnitsGroup(LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup) {
        this.rangeIntegralUnitsGroup = rangeIntegralUnitsGroup;
    }

    public void calculate() {

        rangeIntegrators.forEach(LevelFunctionalIntegrator::prepare);
        rangeIntegralUnitsGroup.prepare();
        rangeIntegralUnitsGroup.boundary().flatMap(commonUnitToPoints).forEach(this::rangeBoundaryIntegrate);

        domainIntegrators.forEach(LevelFunctionalIntegrator::prepare);
        domainIntegralUnitsGroup.prepare();
        domainIntegralUnitsGroup.volume().flatMap(commonUnitToPoints).forEach(this::domainVolumeIntegrate);
        domainIntegralUnitsGroup.boundary().flatMap(commonUnitToPoints).forEach(this::domainBoundaryIntegrate);
    }

    private void rangeBoundaryIntegrate(GeomQuadraturePoint gqp) {
        TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue> pack = levelPackFunction.apply(gqp
                .getGeomPoint().getCoord());
        PartialValue levelValue = pack.getFirst();
        ShapeFunctionValue levelShapeValue = pack.getSecond();
        for (LevelFunctionalIntegrator integrator : rangeIntegrators) {
            integrator.boundaryIntegrate(gqp, levelValue, levelShapeValue);
        }
    }

    private void domainBoundaryIntegrate(GeomQuadraturePoint gqp) {
        TwoTuple<? extends PartialValue, ? extends ShapeFunctionValue> pack = levelPackFunction.apply(gqp
                .getGeomPoint().getCoord());
        PartialValue levelValue = pack.getFirst();
        ShapeFunctionValue levelShapeValue = pack.getSecond();
        for (LevelFunctionalIntegrator integrator : domainIntegrators) {
            integrator.boundaryIntegrate(gqp, levelValue, levelShapeValue);
        }
    }

    private void domainVolumeIntegrate(GeomQuadraturePoint gqp) {
        for (LevelFunctionalIntegrator integrator : domainIntegrators) {
            integrator.volumeIntegrate(gqp, null, null);
        }
    }

    public double value(int i) {
        return getIntegrator(i).value();
    }

    public LevelFunctionalIntegrator getIntegrator(int i) {
        return i < rangeIntegrators.size() ? rangeIntegrators.get(i) : domainIntegrators.get(i
                - rangeIntegrators.size());
    }

    public double[] gradient(int i) {
        return getIntegrator(i).gradient();
    }

    public double[] values() {
        double[] values = new double[size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = value(i);
        }
        return values;
    }

    public double[][] gradients() {
        double[][] gradients = new double[size()][];
        for (int i = 0; i < gradients.length; i++) {
            gradients[i] = gradient(i);
        }
        return gradients;
    }

    public int size() {
        return rangeIntegrators.size() + domainIntegrators.size();
    }

    public void setRangeIntegrators(List<? extends LevelFunctionalIntegrator> rangeIntegrators) {
        this.rangeIntegrators = new ArrayList<>(rangeIntegrators);
    }

    public void setDomainIntegrators(List<? extends LevelFunctionalIntegrator> domainIntegrators) {
        this.domainIntegrators = new ArrayList<>(domainIntegrators);
    }

    public void setGradientSize(int gradientSize) {
        for (int i = 0; i < size(); i++) {
            getIntegrator(i).setGradientSize(gradientSize);
        }
    }

    public class FunctionsGroup {
        public List<DoubleSupplier> getInequalConstraintsValueSuppliers() {
            ArrayList<DoubleSupplier> result = new ArrayList<>();
            for (int i = 0; i < size(); i++) {
                int index = i;
                result.add(() -> value(index));
            }
            return result;
        }

        public List<Supplier<double[]>> getInequalConstraintsGradientSuppliers() {
            ArrayList<Supplier<double[]>> result = new ArrayList<>();
            for (int i = 0; i < size(); i++) {
                int index = i;
                result.add(() -> gradient(index));
            }
            return result;
        }
    }

}
