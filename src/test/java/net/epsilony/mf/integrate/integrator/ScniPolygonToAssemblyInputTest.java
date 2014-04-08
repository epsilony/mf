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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.SimpGeomPoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.shape_func.SimpShapeFunctionValue;
import net.epsilony.mf.util.math.ArrayPartialValueTuple;
import net.epsilony.mf.util.math.ArrayPartialValueTuple.SingleArray;
import net.epsilony.mf.util.math.Pds2;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.solid.GeomUnit;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ScniPolygonToAssemblyInputTest {

    @Test
    public void testByQuadrangle() {
        double[][] vertes = new double[][] { { -0.1, 0.2 }, { 1.1, 0.1 }, { 1.1, 0.9 }, { 0.2, 1.2 } };
        PolygonIntegrateUnit poly = new PolygonIntegrateUnit();
        poly.setVertesCoords(vertes);
        poly.setEmbededIn(new MFNode());

        ScniPolygonToAssemblyInput scni = new ScniPolygonToAssemblyInput();
        MockLoadValueFunction loadValueFunction = new MockLoadValueFunction();
        scni.setLoadValueFunction(loadValueFunction);
        RandomButOneMixer mixer = new RandomButOneMixer();
        scni.setMixer(mixer);
        scni.setQuadratureDegree(2);

        AssemblyInput result = scni.apply(poly);

        assertEquals(1, loadValueFunction.record.size());
        GeomPoint loadRecord = loadValueFunction.record.get(0);
        assertArrayEquals(Math2D.centroid(vertes, null), loadRecord.getCoord(), 1e-12);
        assertTrue(loadRecord.getLoadKey() == poly.getEmbededIn());

        ShapeFunctionValue shapeFuncValue = result.getT2Value().getTestValue();

        ArrayList<Integer> allMixedAsmIds = new ArrayList<>(mixer.allMixedAsmIds);

        double[] gradInt = integrateGradiant(poly, xy -> Arrays.copyOfRange(mixer.oneShapeFunctionValue(xy), 1, 3));
        assertEquals(allMixedAsmIds.size(), shapeFuncValue.size());
        boolean tested = false;
        for (int i = 0; i < shapeFuncValue.size(); i++) {
            int asmId = shapeFuncValue.getNodeAssemblyIndex(i);
            assertTrue(mixer.allMixedAsmIds.contains(asmId));
            if (asmId == mixer.oneAsmIndex) {
                double[] actGradInt = new double[] { shapeFuncValue.get(i, Pds2.U_x), shapeFuncValue.get(i, Pds2.U_y) };
                MathArrays.scaleInPlace(result.getWeight(), actGradInt);
                assertArrayEquals(gradInt, actGradInt, 1e-12);
                tested = true;
            }
        }
        assertTrue(tested);
    }

    private double[] integrateGradiant(PolygonIntegrateUnit poly, Function<double[], double[]> gradFunc) {

        QuadrangleQuadrature qq = new QuadrangleQuadrature();
        qq.setDegree(2);

        double[][] vertesCoords = poly.getVertesCoords();
        TDoubleArrayList t = new TDoubleArrayList(vertesCoords.length * 2);
        Arrays.stream(vertesCoords).forEach((xy) -> t.add(xy));
        qq.setQuadrangle(t.toArray());
        double[] gradInt = new double[2];
        qq.forEach(qp -> {
            double[] grad = gradFunc.apply(qp.coord);
            MathArrays.scaleInPlace(qp.weight, grad);
            gradInt[0] += grad[0];
            gradInt[1] += grad[1];
        });
        return gradInt;

    }

    static class MockLoadValueFunction implements Function<GeomPoint, LoadValue> {
        ArrayList<GeomPoint> record = new ArrayList<GeomPoint>();

        @Override
        public LoadValue apply(GeomPoint t) {
            SimpGeomPoint copy = new SimpGeomPoint();
            copy.setCoord(t.getCoord());
            copy.setGeomCoord(t.getGeomCoord());
            copy.setGeomUnit(t.getGeomUnit());
            copy.setLoadKey(t.getLoadKey());
            record.add(copy);
            return new LoadValue() {

                @Override
                public double value(int dimIndex) {
                    return 0;
                }

                @Override
                public int size() {
                    return 0;
                }
            };

        }
    }

    static class RandomButOneMixer implements MFMixer {
        boolean diffOrderSetted = false;
        List<double[]> outNormal = new ArrayList<>();
        double[] center;

        final int resultSizeRange = 10;
        final int asmIndexSize = 20;
        final int oneAsmIndex = 7;
        Random rand;

        Set<Integer> allMixedAsmIds = new HashSet<>();

        final double u = 1, u_x = 0.2, u_y = 0.3, u_xx = 0, u_xy = 0, u_yy = 0;

        // u_xx = -0.4, u_xy = 0.1, u_yy = 0.7;

        double[] oneShapeFunctionValue(double[] xy) {
            double x = xy[0];
            double y = xy[1];

            double v = u + x * u_x + y * u_y + x * x * u_xx + x * y * u_xy + y * y * u_yy;
            double v_x = u_x + 2 * x * u_xx + y * u_xy;
            double v_y = u_y + 2 * y * u_yy + x * u_xy;
            return new double[] { v, v_x, v_y };
        }

        @Override
        public int getDiffOrder() {
            return 1;
        }

        @Override
        public void setDiffOrder(int diffOrder) {
            if (diffOrder != 0) {
                throw new IllegalArgumentException();
            }
            diffOrderSetted = true;
        }

        @Override
        public void setBoundary(GeomUnit boundary) {

        }

        @Override
        public void setCenter(double[] center) {
            this.center = center;

        }

        @Override
        public void setUnitOutNormal(double[] unitNormal) {
            outNormal.add(unitNormal);
        }

        @Override
        public ShapeFunctionValue mix() {
            if (null == rand) {
                rand = new Random();
            }
            int resultSize = rand.nextInt(resultSizeRange);
            ArrayList<Entry> resultData = new ArrayList<>(resultSize);
            Set<Integer> newIndesSet = new HashSet<>();
            resultData.add(new Entry(oneShapeFunctionValue(center)[0], oneAsmIndex));
            newIndesSet.add(oneAsmIndex);
            if (asmIndexSize <= resultSize) {
                throw new IllegalStateException();
            }
            for (int i = 0; i < resultSize - 1; i++) {
                int newAsmIndex = rand.nextInt(asmIndexSize);
                while (newIndesSet.contains(newAsmIndex)) {
                    newAsmIndex = rand.nextInt(asmIndexSize);
                }
                newIndesSet.add(newAsmIndex);

                Entry newEntry = new Entry(rand.nextDouble(), newAsmIndex);
                resultData.add(newEntry);
            }
            allMixedAsmIds.addAll(newIndesSet);
            Collections.shuffle(resultData, rand);

            SingleArray partialValueTuple = new ArrayPartialValueTuple.SingleArray(resultSize, 2, 0);
            for (int i = 0; i < resultSize; i++) {
                partialValueTuple.set(i, 0, resultData.get(i).data);
            }
            return new SimpShapeFunctionValue(partialValueTuple, i -> resultData.get(i).asmIndex);
        }
    }

    public static class Entry {
        double data;
        int asmIndex;

        public Entry(double data, int asmIndex) {
            this.data = data;
            this.asmIndex = asmIndex;
        }

    }

}
