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
package net.epsilony.mf.integrate.integrator.vc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.epsilony.mf.util.math.ArrayPartialTuple;
import net.epsilony.mf.util.math.ArrayPartialTuple.RowForPartial;
import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialFunction;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.math.PartialVectorFunction;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LinearVCNode2DTest {

    @Test
    public void testVolume() {
        List<double[]> coords = Arrays.asList(new double[] { 0.1, 2.3 }, new double[] { -22, 139 });
        double[] weights = new double[] { -7, 22 };
        MockShapeFunction shapeFunction = new MockShapeFunction();
        shapeFunction.setMaxPartialOrder(1);
        MockBasesFunction basesFunction = new MockBasesFunction();
        LinearVCNode2D sample = new LinearVCNode2D();

        sample.setAssemblyIndex(12);
        assertEquals(12, sample.getAssemblyIndex());

        for (int i = 0; i < weights.length; i++) {
            double[] coord = coords.get(i);
            double weight = weights[i];
            basesFunction.setMaxPartialOrder(1);
            PartialTuple value = basesFunction.value(coord);
            sample.volumeIntegrate(coord, shapeFunction.value(null), value, weight);
        }

        for (int i = 0; i < coords.size(); i++) {
            assertArrayEquals(coords.get(i), basesFunction.coordRecord.get(i), 0);
        }

        DenseMatrix matrix = new DenseMatrix(3, 2);
        assertEquals(weights.length, basesFunction.dataRecord.size());
        for (int i = 0; i < basesFunction.dataRecord.size(); i++) {
            DenseMatrix data = new DenseMatrix(basesFunction.dataRecord.get(i));
            matrix.add(weights[i], data);
        }

        DenseMatrix64F actMat = sample.getMatrix();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                assertEquals(matrix.get(i + 1, j), actMat.get(i, j), 1e-12);
            }
        }

        DenseMatrix64F vector = sample.getVector();
        assertEquals(2, vector.numRows);
        assertEquals(1, vector.numCols);

        DenseVector expVec = new DenseVector(3);
        for (int i = 0; i < shapeFunction.dataRecords.size(); i++) {
            expVec.add(-weights[i], new DenseVector(shapeFunction.dataRecords.get(i)));
        }
        assertEquals(weights.length, shapeFunction.dataRecords.size());
        for (int i = 0; i < 2; i++) {
            assertEquals(expVec.get(i + 1), vector.get(i, 0), 1e-12);
        }
    }

    @Test
    public void testNeumann() {
        List<double[]> coords = Arrays.asList(new double[] { 0.1, 2.3 }, new double[] { -22, 139 });
        List<double[]> normals = Arrays.asList(new double[] { 3.0 / 5, 4.0 / 5 }, new double[] { 0.2, 0.8 });
        double[] weights = new double[] { -7, 22 };
        MockShapeFunction shapeFunction = new MockShapeFunction();
        shapeFunction.setMaxPartialOrder(1);
        MockBasesFunction basesFunction = new MockBasesFunction();
        LinearVCNode2D sample = new LinearVCNode2D();

        sample.setAssemblyIndex(12);
        assertEquals(12, sample.getAssemblyIndex());

        for (int i = 0; i < weights.length; i++) {
            double[] coord = coords.get(i);
            double weight = weights[i];
            basesFunction.setMaxPartialOrder(0);
            PartialTuple value = basesFunction.value(coord);
            sample.boundaryIntegrate(coord, shapeFunction.value(null), value, weight, normals.get(i));
        }

        for (int i = 0; i < coords.size(); i++) {
            assertArrayEquals(coords.get(i), basesFunction.coordRecord.get(i), 0);
        }

        DenseMatrix expMatrix = new DenseMatrix(2, 2);
        assertEquals(weights.length, basesFunction.dataRecord.size());
        for (int i = 0; i < basesFunction.dataRecord.size(); i++) {
            DenseMatrix data = new DenseMatrix(basesFunction.dataRecord.get(i));
            DenseMatrix normal = new DenseMatrix(new double[][] { normals.get(i) });
            normal.transAmultAdd(-weights[i], data, expMatrix);
        }

        DenseMatrix64F actMat = sample.getMatrix();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                assertEquals(expMatrix.get(i, j), actMat.get(i, j), 1e-12);
            }
        }

        DenseMatrix64F vector = sample.getVector();
        assertEquals(2, vector.numRows);
        assertEquals(1, vector.numCols);

        DenseVector expVec = new DenseVector(2);
        for (int i = 0; i < shapeFunction.dataRecords.size(); i++) {
            expVec.add(weights[i] * shapeFunction.dataRecords.get(i)[0], new DenseVector(normals.get(i)));
        }
        assertEquals(weights.length, shapeFunction.dataRecords.size());
        for (int i = 0; i < 2; i++) {
            assertEquals(expVec.get(i), vector.get(i, 0), 1e-12);
        }
    }

    @Test
    public void testSolveAndValue() {
        AbstractVCNode sample = new LinearVCNode2D();
        DenseMatrix64F matrix = sample.getMatrix();
        matrix.set(0, 0, 1);
        matrix.set(1, 1, 1);
        DenseMatrix64F vector = sample.getVector();
        vector.set(0, 0, 7);
        vector.set(1, 0, -2);
        sample.solve();
        MockBasesFunction baseFunction = new MockBasesFunction();

        baseFunction.setMaxPartialOrder(1);

        double[] vc = sample.getVC();
        assertArrayEquals(vector.data, vc, 1e-12);

        double[][] coords = new double[][] { { 0.1, 0.2 }, { 0.3, 0.4 } };
        for (double[] coord : coords) {
            PartialValue act = sample.value(baseFunction, coord);
            double[] actCoord = baseFunction.coordRecord.get(baseFunction.coordRecord.size() - 1);
            assertArrayEquals(coord, actCoord, 0);
            double[][] base = baseFunction.dataRecord.get(baseFunction.dataRecord.size() - 1);
            DenseMatrix baseMat = new DenseMatrix(base);
            Vector exp = baseMat.mult(new DenseVector(vector.data), new DenseVector(3));
            for (int i = 0; i < exp.size(); i++) {
                assertEquals(exp.get(i), act.get(i), 1e-12);
            }
        }

    }

    public static class MockShapeFunction implements PartialFunction {
        ArrayList<double[]> dataRecords = new ArrayList<>();
        Random random = new Random();
        private int maxPartialOrder;

        @Override
        public int getSpatialDimension() {
            return 2;
        }

        @Override
        public int getMaxPartialOrder() {
            return maxPartialOrder;
        }

        @Override
        public void setMaxPartialOrder(int maxPartialOrder) {
            this.maxPartialOrder = maxPartialOrder;
        }

        @Override
        public PartialValue value(double[] coord) {
            ArrayPartialValue result = new ArrayPartialValue(getSpatialDimension(), getMaxPartialOrder());
            for (int i = 0; i < result.partialSize(); i++) {

                result.set(i, random.nextDouble());
            }
            dataRecords.add(result.getData());
            return result;
        }

    }

    public static class MockBasesFunction implements PartialVectorFunction {
        Random rand = new Random();
        private int maxPartialOrder;
        ArrayList<double[][]> dataRecord = new ArrayList<>();
        ArrayList<double[]> coordRecord = new ArrayList<>();

        @Override
        public int size() {
            return 2;
        }

        @Override
        public int getSpatialDimension() {
            return 2;
        }

        @Override
        public int getMaxPartialOrder() {
            return maxPartialOrder;
        }

        @Override
        public void setMaxPartialOrder(int maxPartialOrder) {
            this.maxPartialOrder = maxPartialOrder;

        }

        @Override
        public PartialTuple value(double[] coord) {
            double[][] data = genRandomData();
            dataRecord.add(data);
            coordRecord.add(coord);
            ArrayPartialTuple.RowForPartial result = new RowForPartial(size(), getSpatialDimension(),
                    getMaxPartialOrder(), data);
            return result;
        }

        private double[][] genRandomData() {
            double[][] result = new double[partialSize()][size()];
            for (double[] d : result) {
                for (int i = 0; i < d.length; i++) {
                    d[i] = rand.nextDouble();
                }
            }
            return result;
        }
    }

}
