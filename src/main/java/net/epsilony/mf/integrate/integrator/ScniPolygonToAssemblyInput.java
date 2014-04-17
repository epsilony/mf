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

import gnu.trove.list.array.TIntArrayList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.SimpGeomPoint;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.RawAssemblyInput;
import net.epsilony.mf.process.assembler.SymmetricT2Value;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.shape_func.SimpShapeFunctionValue;
import net.epsilony.mf.util.math.ArrayPartialTuple;
import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;
import net.epsilony.mf.util.math.Pds2;
import net.epsilony.tb.analysis.Math2D;

import org.apache.commons.math3.util.MathArrays;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ScniPolygonToAssemblyInput implements Function<PolygonIntegrateUnit, AssemblyInput> {
    private MFMixer mixer;
    private Function<GeomPoint, LoadValue> loadValueFunction;
    private final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();
    private final Map<Integer, IntegralValue> integralMap = new LinkedHashMap<>();

    @Override
    public AssemblyInput apply(PolygonIntegrateUnit poly) {

        integralMap.clear();
        double[][] vertesCoords = poly.getVertesCoords();
        double[] centroid = Math2D.centroid(vertesCoords, null);

        addCentroidValueToIntegralMap(centroid);

        for (int i = 0; i < vertesCoords.length; i++) {
            double[] start = vertesCoords[i];
            double[] end = vertesCoords[(i + 1) % vertesCoords.length];

            linearQuadratureSupport.setStartEndCoords(start, end);
            while (linearQuadratureSupport.hasNext()) {
                linearQuadratureSupport.next();
                double[] coord = linearQuadratureSupport.getLinearCoord();
                double[] outNormal = Math2D.vectorUnitOutNormal(MathArrays.ebeSubtract(end, start), null);
                mixer.setCenter(coord);
                mixer.setBoundary(null);
                mixer.setUnitOutNormal(outNormal);
                ShapeFunctionValue mix = mixer.mix();
                integrateToMap(mix, outNormal, linearQuadratureSupport.getLinearWeight());
            }
        }

        double area = Math2D.area(vertesCoords);
        SimpGeomPoint geomPoint = new SimpGeomPoint();
        geomPoint.setCoord(centroid);
        geomPoint.setLoadKey(poly.getLoadKey());
        LoadValue loadValue = loadValueFunction.apply(geomPoint);

        RawAssemblyInput result = new RawAssemblyInput();
        result.setLoadValue(loadValue);
        result.setWeight(area);
        result.setT2Value(new SymmetricT2Value(integralMapToShapeFunctionValue(area)));
        return result;
    }

    private ShapeFunctionValue integralMapToShapeFunctionValue(double area) {
        SimpShapeFunctionValue shapeFunctionValue = new SimpShapeFunctionValue();
        SingleArray shapeValue = new ArrayPartialTuple.SingleArray(integralMap.size(), 2, 1);
        Set<Entry<Integer, IntegralValue>> entrySet = integralMap.entrySet();
        int i = 0;
        TIntArrayList nodesAsmIndes = new TIntArrayList(integralMap.size());
        for (Entry<Integer, IntegralValue> en : entrySet) {
            IntegralValue value = en.getValue();
            shapeValue.set(i, 0, value.v);
            shapeValue.set(i, Pds2.U_x, value.dx / area);
            shapeValue.set(i, Pds2.U_y, value.dy / area);
            nodesAsmIndes.add(en.getKey());
            i++;
        }
        shapeFunctionValue.setPartialValueTuple(shapeValue);
        shapeFunctionValue.setAssemblyIndexGetter(nodesAsmIndes::get);
        return shapeFunctionValue;
    }

    private void addCentroidValueToIntegralMap(double[] centroid) {
        mixer.setCenter(centroid);
        mixer.setBoundary(null);
        mixer.setUnitOutNormal(null);
        ShapeFunctionValue mix = mixer.mix();

        for (int i = 0; i < mix.size(); i++) {
            Integer asmId = mix.getNodeAssemblyIndex(i);
            double value = mix.get(i, 0);
            IntegralValue integralValue = integralMap.get(asmId);
            if (integralValue == null) {
                integralValue = new IntegralValue();
                integralMap.put(asmId, integralValue);
            }
            integralValue.v += value;
        }
    }

    private void integrateToMap(ShapeFunctionValue mix, double[] outNormal, double weight) {
        for (int i = 0; i < mix.size(); i++) {
            Integer asmIndex = mix.getNodeAssemblyIndex(i);
            double value = mix.get(i, 0);
            IntegralValue integralValue = integralMap.get(asmIndex);
            if (null == integralValue) {
                integralValue = new IntegralValue();
                integralMap.put(asmIndex, integralValue);
            }
            integralValue.dx += weight * value * outNormal[0];
            integralValue.dy += weight * value * outNormal[1];
        }
    }

    private static class IntegralValue {
        double v, dx, dy;
    }

    public int getQuadratureDegree() {
        return linearQuadratureSupport.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        linearQuadratureSupport.setQuadratureDegree(quadratureDegree);
    }

    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
        mixer.setDiffOrder(0);
    }

    public void setLoadValueFunction(Function<GeomPoint, LoadValue> loadValueFunction) {
        this.loadValueFunction = loadValueFunction;
    }

}
