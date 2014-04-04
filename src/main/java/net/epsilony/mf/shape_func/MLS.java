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

import gnu.trove.list.array.TDoubleArrayList;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.analysis.WithDiffOrderUtil;
import net.epsilony.tb.common_func.BasesFunction;
import net.epsilony.tb.common_func.MonomialBases;
import net.epsilony.tb.common_func.RadialBasis;

import org.apache.commons.math3.util.FastMath;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MLS implements MFShapeFunction {

    int id;
    RadialBasis weightFunc = new RadialBasis();
    BasesFunction basesFunc = new MonomialBases();
    MLSCacheNew cache = new MLSCacheNew();
    double[] ZEROS;
    private TDoubleArrayList[] distances = null;
    private List<MFNode> nodes;
    private double[] position;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setDimension(int dim) {
        if (dim < 1 || dim > 3) {
            throw new IllegalArgumentException("only supports dim 1-3, not " + dim);
        }
        weightFunc.setDimension(dim);
        basesFunc.setDimension(dim);
        ZEROS = new double[dim];
    }

    @Override
    public int getDimension() {
        return weightFunc.getDimension();
    }

    @Override
    public int getDiffOrder() {
        return weightFunc.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException("only support diffOrder that is 0 or 1, not " + diffOrder);
        }
        weightFunc.setDiffOrder(diffOrder);
    }

    public MLS() {
        setDiffOrder(0);
        setDimension(2);
    }

    @Override
    public ShapeFunctionValue values() {
        int nodesSize = nodes.size();
        int diffOrder = getDiffOrder();
        int dimension = getDimension();

        int diffSize = WithDiffOrderUtil.outputLength(dimension, diffOrder);

        cache.setup(basesFunc.basesSize(), diffOrder, dimension);
        Material material = cache.getByNodesSize(nodesSize);

        calcMatAB(material);

        basesFunc.setDiffOrder(diffOrder);

        DenseMatrix64F[] resultsWraps = material.getResultsWrappers();
        DenseMatrix64F[] gammas = material.getGammas();
        DenseMatrix64F gamma = gammas[0];
        double[][] basesByDiff = material.getBases();
        basesFunc.values(ZEROS, basesByDiff);
        DenseMatrix64F[] basesByDiffWrap = material.getBasesWrappers();
        DenseMatrix64F matA = material.getMatAs()[0];

        solve(matA, basesByDiffWrap[0], gamma);
        DenseMatrix64F matB = material.getMatBs()[0];
        CommonOps.multTransA(matB, gamma, resultsWraps[0]);

        if (diffOrder > 0) {

            DenseMatrix64F tv = material.getTempGamma();
            DenseMatrix64F tv2 = material.getTempResult();
            tv2.numRows = nodesSize;
            tv2.numCols = 1;
            DenseMatrix64F[] matAs = material.getMatAs();
            DenseMatrix64F[] matBs = material.getMatBs();
            DenseMatrix64F gamma_d = gammas[1];
            for (int i = 1; i < diffSize; i++) {
                CommonOps.mult(matAs[i], gamma, tv);
                CommonOps.add(-1, tv, basesByDiffWrap[i], tv);
                solve(matA, tv, gamma_d);
                CommonOps.multTransA(matB, gamma_d, tv2);
                DenseMatrix64F matB_d = matBs[i];
                CommonOps.multTransA(matB_d, gamma, resultsWraps[i]);
                CommonOps.addEquals(resultsWraps[i], tv2);
            }
        }

        checkResult(material);

        fillShapeFunctionNodesAsmIds(material);

        return material.getFormalResult();
    }

    private void fillShapeFunctionNodesAsmIds(Material material) {
        int[] nodesAsmIds = material.getNodesAsmIds();
        int i = 0;
        for (MFNode node : nodes) {
            nodesAsmIds[i] = node.getAssemblyIndex();
            i++;
        }
    }

    private void checkResult(Material material) {
        double[][] results = material.getResults();
        for (double[] result : results) {
            for (double d : result) {
                if (!Double.isFinite(d)) {
                    throw new IllegalStateException();
                }
            }
        }
    }

    private void calcMatAB(Material material) {
        int nodeIndex = 0;
        initMatAB(material);
        double[] weights = new double[1 + getDimension()];
        double[] dist = new double[1 + getDimension()];
        double[] tCoord = new double[getDimension()];
        basesFunc.setDiffOrder(0);
        double[][] bases = material.getBases();
        for (MFNode nd : nodes) {
            getDist(nd, nodeIndex, dist);
            double infRad = nd.getInfluenceRadius();
            weightFunc.values(dist, infRad, weights);
            for (int i = 0; i < tCoord.length; i++) {
                tCoord[i] = nd.getCoord()[i] - position[i];
            }
            basesFunc.values(tCoord, bases);
            pushToMatA(weights, material);
            pushToMatB(weights, material, nodeIndex);
            nodeIndex++;
        }
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + '{' + "weightFunc=" + weightFunc + ", basesFunc=" + basesFunc
                + '}';
    }

    private void solve(DenseMatrix64F matA, DenseMatrix64F b, DenseMatrix64F x) {
        if (!CommonOps.solve(matA, b, x)) {
            throw new IllegalStateException();
        }
    }

    private void getDist(MFNode nd, int node_index, double[] dist) {
        if (distances != null) {
            for (int i = 0; i < dist.length; i++) {
                dist[i] = distances[node_index].get(i);
            }
            return;
        }
        double[] coord = nd.getCoord();
        double d = 0;
        for (int i = 0; i < getDimension(); i++) {
            double t = -coord[i] + position[i];

            d += t * t;
            if (getDiffOrder() >= 1) {
                dist[i + 1] = t;
            }
        }
        d = FastMath.sqrt(d);
        dist[0] = d;
        if (d == 0 || getDiffOrder() < 1) {
            return;
        }
        for (int i = 1; i < dist.length; i++) {
            dist[i] /= d;
        }
    }

    private void initMatAB(Material material) {
        DenseMatrix64F[] matAs = material.getMatAs();
        DenseMatrix64F[] matBs = material.getMatBs();
        for (DenseMatrix64F matA : matAs) {
            CommonOps.fill(matA, 0);
        }
        for (DenseMatrix64F matB : matBs) {
            CommonOps.fill(matB, 0);
        }
    }

    private void pushToMatA(double[] weights, Material material) {
        int numDiffs = WithDiffOrderUtil.outputLength(getDimension(), getDiffOrder());
        DenseMatrix64F tMat = material.getTempMatA();
        DenseMatrix64F[] matAs = material.getMatAs();
        DenseMatrix64F basesWrapper = material.getBasesWrappers()[0];
        for (int i = 0; i < numDiffs; i++) {
            double weight = weights[i];
            DenseMatrix64F matA = matAs[i];
            CommonOps.multOuter(basesWrapper, tMat);
            CommonOps.add(weight, tMat, matA, matA);
        }
    }

    private void pushToMatB(double[] weights, Material material, int nodeIndex) {
        int numDiffs = WithDiffOrderUtil.outputLength(getDimension(), getDiffOrder());
        DenseMatrix64F[] matBs = material.getMatBs();
        DenseMatrix64F getBasesWrapper = material.getBasesWrappers()[0];
        for (int i = 0; i < numDiffs; i++) {
            DenseMatrix64F matB = matBs[i];
            double weight = weights[i];
            for (int j = 0; j < basesFunc.basesSize(); j++) {
                matB.set(j, nodeIndex, weight * getBasesWrapper.get(j));
            }
        }
    }

    @Override
    public void setNodes(List<MFNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public void setPosition(double[] position) {
        this.position = position;
    }

    @Override
    public void setDistancesToPosition(TDoubleArrayList[] distances) {
        this.distances = distances;
    }

    public RadialBasis getWeightFunc() {
        return weightFunc;
    }

    public void setWeightFunc(RadialBasis weightFunc) {
        this.weightFunc = weightFunc;
    }

    public BasesFunction getBasesFunc() {
        return basesFunc;
    }

    public void setBasesFunc(BasesFunction basesFunc) {
        this.basesFunc = basesFunc;
    }

    @Override
    public boolean isValueIndependent() {
        return false;
    }

    static class MLSCacheNew {
        int basesSize = -1;
        int diffOrder = -1;
        int dimension = -1;
        boolean needReset = false;
        private static final int CLEAN_THRESHOLD = 60;
        private static final int CLEAN_MAP_SIZE = 100;
        int emptyReferenceCount = 0;
        Map<Integer, SoftReference<Material>> nodesSizeMaterialMap = new LinkedHashMap<>();

        public void setup(int basesSize, int diffOrder, int dimension) {
            if (basesSize <= 0 || diffOrder < 0 || dimension <= 0) {
                throw new IllegalArgumentException();
            }
            if (this.basesSize != basesSize || this.diffOrder != diffOrder || this.dimension != dimension) {
                reset();
            }
            this.basesSize = basesSize;
            this.diffOrder = diffOrder;
            this.dimension = dimension;
        }

        public Material getByNodesSize(int nodesSize) {

            if (nodesSize <= 0) {
                throw new IllegalArgumentException();
            }

            SoftReference<Material> softReference = nodesSizeMaterialMap.get(nodesSize);

            Material result;
            if (null != softReference) {
                result = softReference.get();
                if (null == result) {
                    emptyReferenceCount++;
                    clearEmptyReferences();
                } else {
                    return result;
                }
            }
            result = new Material(nodesSize, basesSize, diffOrder, dimension);
            nodesSizeMaterialMap.put(nodesSize, new SoftReference<>(result));
            return result;
        }

        private void clearEmptyReferences() {
            if (nodesSizeMaterialMap.size() < CLEAN_MAP_SIZE || emptyReferenceCount < CLEAN_THRESHOLD) {
                return;
            }
            ArrayList<Integer> emptyKeys = new ArrayList<>(CLEAN_THRESHOLD);
            nodesSizeMaterialMap.entrySet().stream().filter((entry) -> entry.getValue().get() == null)
                    .forEach((entry) -> emptyKeys.add(entry.getKey()));
            emptyKeys.stream().forEach(nodesSizeMaterialMap::remove);
            emptyReferenceCount = 0;
        }

        private void reset() {
            emptyReferenceCount = 0;
            nodesSizeMaterialMap.clear();
        }
    }

    private static class Material {

        private final DenseMatrix64F[] gammas;
        private final DenseMatrix64F tempGamma;
        private final DenseMatrix64F[] matAs;
        private final DenseMatrix64F tempMatA;
        private final DenseMatrix64F[] matBs;
        private final double[][] bases;
        private final DenseMatrix64F[] basesWrappers;
        private final double[][] results;
        private final DenseMatrix64F[] resultsWrappers;
        private final DenseMatrix64F tempResult;
        private final ShapeFunctionValue formalResult;
        private final int[] nodesAsmIds;

        public Material(int nodesSize, int basesSize, int diffOrder, int dimension) {
            int diffSize = WithDiffOrderUtil.outputLength(dimension, diffOrder);
            matAs = new DenseMatrix64F[diffSize];
            for (int i = 0; i < matAs.length; i++) {
                matAs[i] = new DenseMatrix64F(basesSize, basesSize);
            }

            tempMatA = new DenseMatrix64F(basesSize, basesSize);

            matBs = new DenseMatrix64F[diffSize];
            for (int i = 0; i < matBs.length; i++) {
                matBs[i] = new DenseMatrix64F(basesSize, nodesSize);
            }

            gammas = new DenseMatrix64F[diffSize];
            for (int i = 0; i < gammas.length; i++) {
                gammas[i] = new DenseMatrix64F(basesSize, 1);
            }

            tempGamma = new DenseMatrix64F(basesSize, 1);

            bases = new double[diffSize][basesSize];
            basesWrappers = new DenseMatrix64F[diffSize];
            for (int i = 0; i < bases.length; i++) {
                basesWrappers[i] = DenseMatrix64F.wrap(basesSize, 1, bases[i]);
            }

            results = new double[diffSize][nodesSize];
            resultsWrappers = new DenseMatrix64F[diffSize];
            for (int i = 0; i < diffSize; i++) {
                resultsWrappers[i] = DenseMatrix64F.wrap(nodesSize, 1, results[i]);
            }

            tempResult = new DenseMatrix64F(nodesSize, 1);

            nodesAsmIds = new int[nodesSize];
            formalResult = new ArrayShapeFunctionValue(dimension, diffOrder, results, nodesAsmIds);
        }

        public DenseMatrix64F[] getGammas() {
            return gammas;
        }

        public DenseMatrix64F getTempGamma() {
            return tempGamma;
        }

        public DenseMatrix64F[] getMatAs() {
            return matAs;
        }

        public DenseMatrix64F getTempMatA() {
            return tempMatA;
        }

        public DenseMatrix64F[] getMatBs() {
            return matBs;
        }

        public double[][] getBases() {
            return bases;
        }

        public DenseMatrix64F[] getBasesWrappers() {
            return basesWrappers;
        }

        public double[][] getResults() {
            return results;
        }

        public DenseMatrix64F[] getResultsWrappers() {
            return resultsWrappers;
        }

        public DenseMatrix64F getTempResult() {
            return tempResult;
        }

        public ShapeFunctionValue getFormalResult() {
            return formalResult;
        }

        public int[] getNodesAsmIds() {
            return nodesAsmIds;
        }

    }

}
