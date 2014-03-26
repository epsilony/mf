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

package net.epsilony.mf.shape_func;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.shape_func.config.MLSConfig;
import net.epsilony.tb.EYArrays;
import net.epsilony.tb.TestTool;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MLSTest {

    double[][] nodesCoordsRanges = new double[][] { { 1.1, 11.3 }, { -2, 7.9 }, { 3, 13 } };
    int numOfNodesPerDimension = 11;
    double influenceRadMean = 10 / (numOfNodesPerDimension - 1) * 3;
    double influenceRadVibration = 0.1;
    long influenceRadRandomSeed = 47;
    //
    double[][] sampleCoordsRanges = new double[][] { { 1.6, 5.6 }, { 0, 4 }, { 7, 11 } };
    int numOfSamplesPerDim = 3;

    List<double[]> genCoords(int dim, double[][] coordsRanges, int numPerDim) {
        double[][] posPerDim = new double[dim][];
        for (int d = 0; d < dim; d++) {
            posPerDim[d] = TestTool.linSpace(coordsRanges[d][0], coordsRanges[d][1], numPerDim);
        }
        LinkedList<double[]> coords = new LinkedList<>();
        int[] indes = new int[dim];
        while (indes[0] < posPerDim[0].length) {
            double[] coord = new double[dim];
            for (int d = 0; d < dim; d++) {
                coord[d] = posPerDim[d][indes[d]];
            }
            coords.add(coord);
            for (int d = dim - 1; d >= 0; d--) {
                indes[d]++;
                if (indes[d] < posPerDim[d].length) {
                    break;
                } else if (d > 0) {
                    indes[d] = 0;
                }
            }
        }
        return coords;
    }

    List<MFNode> genNodes(int dim) {
        LinkedList<MFNode> nodes = new LinkedList<>();
        Random rand = new Random(influenceRadRandomSeed);
        List<double[]> coords = genCoords(dim, nodesCoordsRanges, numOfNodesPerDimension);
        int nodeId = 0;
        for (double[] crd : coords) {
            MFNode nd = new MFNode(crd);
            nd.setId(nodeId);
            nd.setInfluenceRadius(influenceRadMean * (1 + influenceRadVibration * (rand.nextDouble() - 0.5)));
            nodes.add(nd);
            nodeId++;
        }
        return nodes;
    }

    List<double[]> genSamplePts(int dim) {
        return genCoords(dim, sampleCoordsRanges, numOfSamplesPerDim);
    }

    static double[] polynomials(double[] pos, int dim) {
        double x, y, z;
        switch (dim) {
        case 1:
            x = pos[0];
            return new double[] { -3.3 + 4 * x - 2 * x * x, 4 - 4 * x };
        case 2:
            x = pos[0];
            y = pos[1];
            return new double[] { 1.3 - 2.7 * x + 3.3 * y + 0.2 * x * x + 0.3 * x * y - 0.4 * y * y,
                    -2.7 + 0.4 * x + 0.3 * y, 3.3 + 0.3 * x - 0.8 * y };
        case 3:
            x = pos[0];
            y = pos[1];
            z = pos[2];
            return new double[] {
                    1.1 - 2.1 * x + 3 * y + 0.4 * z - x * x + 0.8 * x * y + 0.3 * y * y - x * z + 0.2 * y * z + 0.7 * z
                            * z, -2.1 - 2 * x + 0.8 * y - z, 3 + 0.8 * x + 0.6 * y + 0.2 * z,
                    0.4 - x + 0.2 * y + 1.4 * z };
        default:
            throw new IllegalStateException();
        }
    }

    public static interface SampleFunc {

        double[] val(double[] pos);
    }

    public static class SamplePoly implements SampleFunc {

        int dim;

        public SamplePoly(int dim) {
            this.dim = dim;
        }

        @Override
        public double[] val(double[] pos) {
            return polynomials(pos, dim);
        }
    }

    List<Map<String, Object>> genTestDatas() {
        LinkedList<Map<String, Object>> result = new LinkedList<>();
        for (int dim = 1; dim <= 3; dim++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("dim", dim);
            map.put("polynomialFunction", new SamplePoly(dim));
            map.put("nodes", genNodes(dim));
            map.put("samplePts", genSamplePts(dim));
            result.add(map);
        }
        return result;
    }

    @Test
    public void testPartitionOfUnity() {
        MLS mls = new MLS();
        List<Map<String, Object>> datas = genTestDatas();
        for (Map<String, Object> data : datas) {
            _testPartionOfUnity(mls, data);
        }
    }

    @Test
    public void testPersistPartitionOfUnity() {
        @SuppressWarnings("resource")
        MLS mls = new AnnotationConfigApplicationContext(MLSConfig.class).getBean("shapeFunctionPrototype", MLS.class);
        List<Map<String, Object>> datas = genTestDatas();
        for (Map<String, Object> data : datas) {
            _testPartionOfUnity(mls, data);
        }

    }

    @SuppressWarnings("unchecked")
    public void _testPartionOfUnity(MLS mls, Map<String, Object> data) {
        System.out.println("dim: " + data.get("dim"));
        int dim = (int) data.get("dim");
        double[] exp = new double[dim + 1];
        exp[0] = 1;
        List<double[]> samplePts = (List<double[]>) data.get("samplePts");
        List<MFNode> nodes = (List<MFNode>) data.get("nodes");
        mls.setDiffOrder(1);
        mls.setDimension((int) data.get("dim"));
        boolean tested = false;
        for (double[] pt : samplePts) {
            tested = true;
            List<MFNode> nds = searchNodes(pt, nodes);
            mls.setPosition(pt);
            mls.setNodes(nds);
            double[][] vals = mls.values().arrayForm();
            double[] acts = new double[dim + 1];
            for (int i = 0; i < acts.length; i++) {
                acts[i] = EYArrays.sum(vals[i]);
            }
            assertArrayEquals(exp, acts, 3e-15); // best try
        }
        assertTrue(tested);
    }

    @Test
    public void testFitness() {
        MLS mls = new MLS();
        List<Map<String, Object>> datas = genTestDatas();
        for (Map<String, Object> data : datas) {
            _testFitness(mls, data);
        }
    }

    @SuppressWarnings("unchecked")
    public void _testFitness(MLS mls, Map<String, Object> data) {
        System.out.println("dim: " + data.get("dim"));
        int dim = (int) data.get("dim");
        List<double[]> samplePts = (List<double[]>) data.get("samplePts");
        List<MFNode> nodes = (List<MFNode>) data.get("nodes");
        mls.setDiffOrder(1);
        mls.setDimension((int) data.get("dim"));
        SampleFunc funcs = (SampleFunc) data.get("polynomialFunction");
        boolean tested = false;
        for (double[] pt : samplePts) {
            tested = true;
            List<MFNode> nds = searchNodes(pt, nodes);
            mls.setNodes(nds);
            mls.setPosition(pt);
            double[][] vals = mls.values().arrayForm();
            double[] acts = new double[dim + 1];
            int j = 0;
            for (MFNode node : nds) {
                double cv = funcs.val(node.getCoord())[0];
                for (int d = 0; d <= dim; d++) {
                    acts[d] += vals[d][j] * cv;
                }
                j++;
            }
            double[] exps = funcs.val(pt);
            for (int id = 0; id < exps.length; id++) {
                final double err = 2e-14;// best try
                assertEquals(exps[id], acts[id], Math.max(err, Math.abs(exps[id] * err)));
            }
        }
        assertTrue(tested);
    }

    public static List<MFNode> searchNodes(double[] center, List<MFNode> nodes) {
        List<MFNode> result = new LinkedList<>();
        for (MFNode nd : nodes) {
            double rad = nd.getInfluenceRadius();
            double dst = MathArrays.distance(nd.getCoord(), center);
            if (dst < rad) {
                result.add(nd);
            }
        }
        return result;
    }
}
