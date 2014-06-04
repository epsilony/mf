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

package net.epsilony.mf.process.solver;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntByteHashMap;
import java.util.Arrays;
import net.epsilony.mf.util.matrix.ByteHashRowMatrix;
import net.epsilony.mf.util.matrix.MFMatrix;
import no.uib.cipr.matrix.MatrixEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ReverseCuthillMcKeeMFMatrixResortor {

    private static final Logger logger                        = LoggerFactory
                                                                      .getLogger(ReverseCuthillMcKeeMFMatrixResortor.class);
    private static final int    DEFAULT_BAND_WIDTH_ESTIMATION = 30;
    private final MFMatrix      matrix;
    private ByteHashRowMatrix   graphMatrix;
    private int[]               opt2ori;
    private int[]               ori2opt;
    private int                 originalBandWidth;
    private int                 optimiziedBandWidth;

    public void sortVector(MFMatrix vector, MFMatrix result) {
        checkVectorsSizes(vector, result);

        for (int i = 0; i < result.numRows(); i++) {
            result.set(ori2opt[i], 0, vector.get(i, 0));
        }
    }

    public void recoverVector(MFMatrix vector, MFMatrix result) {
        checkVectorsSizes(vector, result);
        for (int i = 0; i < vector.numRows(); i++) {
            result.set(opt2ori[i], 0, vector.get(i, 0));
        }
    }

    public void writeOptimizedMatrixTo(MFMatrix result) {
        if (result.numCols() != matrix.numCols() || result.numRows() != matrix.numRows()) {
            throw new IllegalArgumentException();
        }
        for (MatrixEntry me : matrix) {
            double value = me.get();
            if (value == 0) {
                continue;
            }
            if (result.isUpperSymmetric() && me.column() < me.row()) {
                continue;
            }
            int optRow = ori2opt[me.row()];
            int optCol = ori2opt[me.column()];
            result.set(optRow, optCol, value);
        }
    }

    private void checkVectorsSizes(MFMatrix vector, MFMatrix result) throws IllegalArgumentException {
        if (vector == result) {
            throw new IllegalArgumentException("input vector and output result cannot be the same object");
        }

        if (vector.numCols() != 1 || vector.numRows() != matrix.numRows() || result.numCols() != 1
                || result.numRows() != matrix.numRows()) {
            throw new IllegalArgumentException();
        }
    }

    public int getOriginalBandWidth() {
        return originalBandWidth;
    }

    public int getOptimiziedBandWidth() {
        return optimiziedBandWidth;
    }

    public ReverseCuthillMcKeeMFMatrixResortor(MFMatrix matrix) {
        checkMatrix(matrix);
        this.matrix = matrix;
        logger.info("recieved matrix {}", matrix);
        genGraphMat();
        reverseCuthillMcKee(0);
        logger.info("RCM OK! original/optimized bandwidth {}/{}", originalBandWidth, optimiziedBandWidth);
    }

    private void checkMatrix(MFMatrix matrix) {
        if (matrix.numCols() != matrix.numRows() || matrix.numRows() <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private void genGraphMat() {
        graphMatrix = new ByteHashRowMatrix(matrix.numRows(), matrix.numCols());

        for (MatrixEntry me : matrix) {
            if (matrix.isUpperSymmetric() && me.column() < me.row()) {
                continue;
            }
            if (me.get() != 0) {
                graphMatrix.set(me.row(), me.column(), 1);
                graphMatrix.set(me.column(), me.row(), 1);
            }
        }
    }

    private void reverseCuthillMcKee(int start) {
        final int size = matrix.numRows();
        TIntArrayList indes = new TIntArrayList(size);
        int[] distances = new int[size];
        boolean[] visited = new boolean[size];
        int visitedSearchStart = 0;
        TIntArrayList opt2oriList = new TIntArrayList();
        do {
            int pseudoPeripheralNode = pseudoPeripheralNode(start);
            logger.debug("pseudo-peripheral node: {}", pseudoPeripheralNode);
            broadFirstSearch(pseudoPeripheralNode, indes, distances);
            opt2oriList.addAll(indes);
            if (opt2oriList.size() >= size) {
                break;
            } else {
                for (int i = 0; i < indes.size(); i++) {
                    visited[indes.getQuick(i)] = true;
                }
                for (int i = visitedSearchStart; i < visited.length; i++) {
                    if (!visited[i]) {
                        start = i;
                        visitedSearchStart = i + 1;
                        break;
                    }
                }
            }
        } while (true);
        opt2oriList.reverse();
        opt2ori = opt2oriList.toArray();
        genOri2Opt();
        genBandWidth();
    }

    int pseudoPeripheralNode(int start) {
        int size = matrix.numRows();
        TIntArrayList visitRoute = new TIntArrayList(size);
        int[] distanceToNode = new int[size];
        int maxDistance = 0;
        int node = start;
        do {
            broadFirstSearch(node, visitRoute, distanceToNode);
            int distance = distanceToNode[visitRoute.getQuick(visitRoute.size() - 1)];
            if (distance <= maxDistance) {
                break;
            } else {
                maxDistance = distance;
                int _minDegree = Integer.MAX_VALUE;
                for (int i = visitRoute.size() - 1; i >= 0 && distanceToNode[visitRoute.getQuick(i)] == distance; i--) {
                    int eccNode = visitRoute.getQuick(i);
                    int deg = degree(eccNode);
                    if (deg < _minDegree) {
                        node = eccNode;
                        _minDegree = deg;
                    }
                }
            }
        } while (true);
        return node;
    }

    int degree(int node) {
        return graphMatrix.getRow(node).size() - 1;
    }

    void broadFirstSearch(int start, TIntArrayList visitRoute, int[] distanceToStart) {
        Arrays.fill(distanceToStart, -1);
        visitRoute.resetQuick();
        visitRoute.add(start);
        distanceToStart[start] = 0;
        int[] keys = new int[DEFAULT_BAND_WIDTH_ESTIMATION];
        for (int i = 0; i < visitRoute.size(); i++) {
            int node = visitRoute.getQuick(i);
            TIntByteHashMap nodeRow = graphMatrix.getRow(node);
            keys = nodeRow.keys(keys);
            int keysSize = nodeRow.size();
            for (int j = 0; j < keysSize; j++) {
                int neighbor = keys[j];
                if (neighbor == node) {
                    continue;
                }
                if (distanceToStart[neighbor] < 0) {
                    visitRoute.add(neighbor);
                    distanceToStart[neighbor] = distanceToStart[node] + 1;
                }
            }
            if (visitRoute.size() >= matrix.numRows()) {
                break;
            }
        }
    }

    void genBandWidth() {
        int oriB = 0;
        int optB = 0;
        int[] keys = new int[DEFAULT_BAND_WIDTH_ESTIMATION];
        for (int i = 0; i < matrix.numRows(); i++) {
            TIntByteHashMap row = graphMatrix.getRow(i);
            keys = row.keys(keys);
            int keysSize = row.size();
            int optI = ori2opt[i];
            for (int j = 0; j < keysSize; j++) {
                int neighbor = keys[j];
                if (neighbor == i) {
                    continue;
                }
                int dst = Math.abs(neighbor - i);
                if (dst > oriB) {
                    oriB = dst;
                }
                int optDst = Math.abs(ori2opt[neighbor] - optI);
                if (optDst > optB) {
                    optB = optDst;
                }
            }
        }
        originalBandWidth = oriB;
        optimiziedBandWidth = optB;
    }

    void genOri2Opt() {
        ori2opt = new int[matrix.numRows()];
        for (int i = 0; i < opt2ori.length; i++) {
            ori2opt[opt2ori[i]] = i;
        }
    }

}
