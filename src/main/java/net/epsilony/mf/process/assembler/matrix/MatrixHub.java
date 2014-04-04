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
package net.epsilony.mf.process.assembler.matrix;

import java.util.ArrayList;
import java.util.List;

import net.epsilony.mf.util.bus.ConsumerBus;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import no.uib.cipr.matrix.MatrixEntry;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MatrixHub {

    private MatrixFactory<? extends MFMatrix> mainMatrixFactory, mainVectorFactory;
    private List<MFMatrix> mainMatries, mainVectors;
    private ConsumerBus<MFMatrix> mainMatrixBus, mainVectorBus;
    private MatrixMerger matrixMerger;
    private int valueNodesNum, lagrangleNodesNum;
    private int valueDimension;
    private MFMatrix mergedMainMatrix = null, mergedMainVector = null;

    public void post() {
        mainMatries = new ArrayList<>();
        mainVectors = new ArrayList<>();

        int rowSize = valueDimension * (valueNodesNum + lagrangleNodesNum);

        mainMatrixFactory.setNumRows(rowSize);
        mainMatrixFactory.setNumCols(rowSize);

        mainVectorFactory.setNumCols(1);
        mainVectorFactory.setNumRows(rowSize);

        mainMatrixBus.postToEach(this::produceMainMatrix);
        mainVectorBus.postToEach(this::produceMainVector);

        mergedMainVector = null;
        mergedMainMatrix = null;
    }

    private MFMatrix produceMainMatrix() {
        MFMatrix mfMatrix = mainMatrixFactory.get();
        mainMatries.add(mfMatrix);
        return mfMatrix;
    }

    private MFMatrix produceMainVector() {
        MFMatrix mfMatrix = mainVectorFactory.get();
        mainVectors.add(mfMatrix);
        return mfMatrix;
    }

    public void mergePosted() {
        mergedMainMatrix = mainMatries.get(0);
        mergedMainVector = mainVectors.get(0);
        int lagrangleSize = lagrangleNodesNum * valueDimension;
        if (lagrangleSize > 0) {
            ((LagrangleMatrixMerger) matrixMerger).setLagrangleSize(lagrangleSize);
        }
        for (int i = 1; i < mainMatries.size(); i++) {
            matrixMerger.setDestiny(mergedMainMatrix);
            matrixMerger.setSource(mainMatries.get(i));
            matrixMerger.merge();

            for (MatrixEntry me : mainVectors.get(i)) {
                mergedMainVector.add(me.row(), me.column(), me.get());
            }
        }
    }

    public void clearPosted() {
        mainMatries.clear();
        mainMatrixBus.post(null);
        mainVectors.clear();
        mainVectorBus.post(null);
    }

    public MatrixFactory<? extends MFMatrix> getMainMatrixFactory() {
        return mainMatrixFactory;
    }

    public MatrixFactory<? extends MFMatrix> getMainVectorFactory() {
        return mainVectorFactory;
    }

    public void setMainVectorFactory(MatrixFactory<? extends MFMatrix> mainVectorFactory) {
        this.mainVectorFactory = mainVectorFactory;
    }

    public void setMainMatrixFactory(MatrixFactory<? extends MFMatrix> mainMatrixFactory) {
        this.mainMatrixFactory = mainMatrixFactory;
    }

    public void setMainMatrixBus(ConsumerBus<MFMatrix> mainMatrixBus) {
        this.mainMatrixBus = mainMatrixBus;
    }

    public ConsumerBus<MFMatrix> getMainVectorBus() {
        return mainVectorBus;
    }

    public void setMainVectorBus(ConsumerBus<MFMatrix> mainVectorBus) {
        this.mainVectorBus = mainVectorBus;
    }

    public MatrixMerger getMatrixMerger() {
        return matrixMerger;
    }

    public void setMatrixMerger(MatrixMerger matrixMerger) {
        this.matrixMerger = matrixMerger;
    }

    public int getValueNodesNum() {
        return valueNodesNum;
    }

    public void setValueNodesNum(int valueNodesNum) {
        this.valueNodesNum = valueNodesNum;
    }

    public int getLagrangleNodesNum() {
        return lagrangleNodesNum;
    }

    public void setLagrangleNodesNum(int lagrangleNodesNum) {
        this.lagrangleNodesNum = lagrangleNodesNum;
    }

    public int getValueDimension() {
        return valueDimension;
    }

    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }

    public MFMatrix getMergedMainMatrix() {
        return mergedMainMatrix;
    }

    public MFMatrix getMergedMainVector() {
        return mergedMainVector;
    }
}
