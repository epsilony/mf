/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.Serializable;
import net.epsilony.tb.analysis.WithDiffOrderUtil;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
class MLSCache implements Serializable {

    static int INDICIAL_CACHE_SIZE = 6;
    int basesSize = -1;
    int nodesSize = -1;
    int diffOrder = -1;
    int diffSize = -1;
    int dim = -1;
    TIntObjectMap<DenseMatrix64F[]> matBCachesMap = new TIntObjectHashMap<>();
    DenseMatrix64F[] matBCaches = null;
    DenseMatrix64F[] matACaches = null;
    DenseMatrix64F[] gammaCaches = null;
    double[][][] basesCaches = null;
    DenseMatrix64F[][] basesCacheWrappers = null;
    private DenseMatrix64F dimSizeCache;

    void setup(int diffOrder, int dim, int basesSize, int nodesSize) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException("only supports diffOrder 0-1, not " + diffOrder);
        }
        if (dim < 1 || dim > 3) {
            throw new IllegalArgumentException("only supports dim 1-3, not " + dim);
        }
        if (basesSize < 1) {
            throw new IllegalArgumentException("basesSize must be positive, not " + basesSize);
        }
        if (nodesSize < 1) {
            throw new IllegalArgumentException("nodesSize must be positive, not " + nodesSize);
        }
        if (this.diffOrder != diffOrder || this.dim != dim) {
            this.diffOrder = diffOrder;
            this.diffSize = WithDiffOrderUtil.outputLength(dim, diffOrder);
            if (this.dim != dim) {
                this.dim = dim;
                this.dimSizeCache = new DenseMatrix64F(dim);
            }
        }
        if (this.basesSize != basesSize) {
            this.basesSize = basesSize;
            newACaches();
            newGammaCaches();
            newBasesCaches();
            matBCachesMap.clear();
            matBCaches = null;
            this.nodesSize = -1;
        }
        if (this.nodesSize != nodesSize) {
            this.nodesSize = nodesSize;
            matBCaches = matBCachesMap.get(nodesSize);
            if (null == matBCaches) {
                newBCaches();
                matBCachesMap.put(nodesSize, matBCaches);
            }
        }
    }

    private void newACaches() {
        matACaches = new DenseMatrix64F[INDICIAL_CACHE_SIZE];
        for (int i = 0; i < matACaches.length; i++) {
            matACaches[i] = new DenseMatrix64F(basesSize, basesSize);
        }
    }

    private void newBCaches() {
        matBCaches = new DenseMatrix64F[INDICIAL_CACHE_SIZE];
        for (int i = 0; i < matBCaches.length; i++) {
            matBCaches[i] = new DenseMatrix64F(basesSize, nodesSize);
        }
    }

    private void newGammaCaches() {
        gammaCaches = new DenseMatrix64F[INDICIAL_CACHE_SIZE];
        for (int i = 0; i < gammaCaches.length; i++) {
            gammaCaches[i] = new DenseMatrix64F(basesSize, 1);
        }
    }

    private void newBasesCaches() {
        basesCaches = new double[INDICIAL_CACHE_SIZE][][];
        basesCacheWrappers = new DenseMatrix64F[INDICIAL_CACHE_SIZE][];
        for (int i = 0; i < basesCaches.length; i++) {
            basesCaches[i] = new double[i + 1][basesSize];
            basesCacheWrappers[i] = new DenseMatrix64F[i + 1];
            for (int j = 0; j < basesCaches[i].length; j++) {
                basesCacheWrappers[i][j] = DenseMatrix64F.wrap(basesSize, 1, basesCaches[i][j]);
            }
        }
    }

    public DenseMatrix64F getGammaCache(int diffIndex) {
        return gammaCaches[diffIndex];
    }

    public DenseMatrix64F getMatACache(int diffIndex) {
        return matACaches[diffIndex];
    }

    public DenseMatrix64F getMatBCache(int diffIndex) {
        return matBCaches[diffIndex];
    }

    public double[][] getBasesCache() {
        return basesCaches[diffSize];
    }

    public DenseMatrix64F[] getBasesCacheWraper() {
        return basesCacheWrappers[diffSize];
    }
}
