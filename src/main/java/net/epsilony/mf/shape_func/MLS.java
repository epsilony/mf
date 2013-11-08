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
import java.util.List;
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
    MLSCache cache = new MLSCache();
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
        _setDimension(dim);
    }

    private void _setDimension(int dim) {
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
        _setDiffOrder(diffOrder);
    }

    private void _setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException("only support diffOrder that is 0 or 1, not " + diffOrder);
        }
        weightFunc.setDiffOrder(diffOrder);
    }

    public MLS() {
        _setDiffOrder(0);
        _setDimension(2);
    }

    @Override
    public double[][] values(double[][] output) {
        int nodesSize = nodes.size();
        int diffOrder = getDiffOrder();
        int dimension = getDimension();
        cache.setup(diffOrder, dimension, basesFunc.basesSize(), nodesSize);

        calcMatAB();

        basesFunc.setDiffOrder(diffOrder);

        int numDiffs = WithDiffOrderUtil.outputLength(dimension, diffOrder);

        DenseMatrix64F[] resultsWraps = new DenseMatrix64F[numDiffs];
        if (null == output) {
            output = new double[numDiffs][];
            for (int i = 0; i < resultsWraps.length; i++) {
                resultsWraps[i] = new DenseMatrix64F(nodesSize, 1);
                output[i] = resultsWraps[i].data;
            }
        } else {
            for (int i = 0; i < resultsWraps.length; i++) {
                resultsWraps[i] = DenseMatrix64F.wrap(nodesSize, 1, output[i]);
            }
        }

        DenseMatrix64F gamma = cache.getGammaCache(0);
        double[][] basesByDiff = cache.getBasesCache();
        basesFunc.values(ZEROS, basesByDiff);
        DenseMatrix64F[] basesByDiffWrap = cache.getBasesCacheWraper();
        DenseMatrix64F matA = cache.getMatACache(0);

        solve(matA, basesByDiffWrap[0], gamma);
        DenseMatrix64F matB = cache.getMatBCache(0);
        CommonOps.multTransA(matB, gamma, resultsWraps[0]);

        if (diffOrder > 0) {
            DenseMatrix64F tv = cache.getGammaCache(numDiffs);

            DenseMatrix64F tv2 = cache.getMatBCache(numDiffs); // magic to get a
                                                               // matrix with
                                                               // more space
                                                               // than
                                                               // nodesSizes
            tv2.numRows = nodesSize;
            tv2.numCols = 1;

            DenseMatrix64F gamma_d = cache.getGammaCache(1);
            for (int i = 1; i < numDiffs; i++) {
                CommonOps.mult(cache.getMatACache(i), gamma, tv);
                CommonOps.add(-1, tv, basesByDiffWrap[i], tv);
                solve(matA, tv, gamma_d);
                CommonOps.multTransA(matB, gamma_d, tv2);
                DenseMatrix64F matB_d = cache.getMatBCache(i);
                CommonOps.multTransA(matB_d, gamma, resultsWraps[i]);
                CommonOps.addEquals(resultsWraps[i], tv2);
            }
        }

        for (int i = 0; i < resultsWraps[0].getNumElements(); i++) {
            double v = resultsWraps[0].data[i];
            if (Double.isInfinite(v) || Double.isNaN(v)) {
                throw new IllegalStateException();
            }
        }

        return output;
    }

    private void calcMatAB() {
        int nodeIndex = 0;
        initMatAB();
        double[] weights = new double[1 + getDimension()];
        double[] dist = new double[1 + getDimension()];
        double[] tCoord = new double[getDimension()];
        basesFunc.setDiffOrder(0);
        double[][] bases = cache.getBasesCache();
        for (MFNode nd : nodes) {
            getDist(nd, nodeIndex, dist);
            double infRad = nd.getInfluenceRadius();
            weightFunc.values(dist, infRad, weights);
            for (int i = 0; i < tCoord.length; i++) {
                tCoord[i] = nd.getCoord()[i] - position[i];
            }
            basesFunc.values(tCoord, bases);
            pushToMatA(weights, cache.getBasesCacheWraper()[0]);
            pushToMatB(weights, cache.getBasesCacheWraper()[0], nodeIndex);
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

    private void initMatAB() {
        int numDiffs = WithDiffOrderUtil.outputLength(getDimension(), getDiffOrder());
        for (int i = 0; i < numDiffs; i++) {
            DenseMatrix64F matA = cache.getMatACache(i);
            DenseMatrix64F matB = cache.getMatBCache(i);
            CommonOps.fill(matA, 0);
            CommonOps.fill(matB, 0);
        }
    }

    private void pushToMatA(double[] weights, DenseMatrix64F bases) {
        int numDiffs = WithDiffOrderUtil.outputLength(getDimension(), getDiffOrder());
        DenseMatrix64F tMat = cache.getMatACache(numDiffs);
        for (int i = 0; i < numDiffs; i++) {
            double weight = weights[i];
            DenseMatrix64F matA = cache.getMatACache(i);
            CommonOps.multOuter(bases, tMat);
            CommonOps.add(weight, tMat, matA, matA);
        }
    }

    private void pushToMatB(double[] weights, DenseMatrix64F bases, int nodeIndex) {
        int numDiffs = WithDiffOrderUtil.outputLength(getDimension(), getDiffOrder());
        for (int i = 0; i < numDiffs; i++) {
            DenseMatrix64F matB = cache.getMatBCache(i);
            double weight = weights[i];
            for (int j = 0; j < basesFunc.basesSize(); j++) {
                matB.set(j, nodeIndex, weight * bases.get(j));
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
}
