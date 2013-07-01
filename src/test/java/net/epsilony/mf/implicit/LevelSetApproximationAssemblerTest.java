/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.common_func.RadialFunctionCore;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LevelSetApproximationAssemblerTest {

    private RadialFunctionCore radialFunctionCore = new RadialFunctionCore() {
        @Override
        public double[] values(double x, double[] results) {
            if (null == results) {
                results = new double[1];
            }
            results[0] = x * weightFunctionRatio;
            return results;
        }

        @Override
        public int getDiffOrder() {
            return 0;
        }

        @Override
        public void setDiffOrder(int diffOrder) {
        }

        @Override
        public RadialFunctionCore synchronizeClone() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public LevelSetApproximationAssemblerTest() {
    }
    double weight = 1.2;
    final double weightFunctionRatio = 2.3;
    double[] load = new double[]{-2.7};
    boolean[] loadValidity = new boolean[]{true};
    double[] shapeFunctionWithNormalSort = new double[]{
        0, 0, 4.2, -3, 2.7, 0, 0, 2.8, 0, 11, 0
    };
    int[] sampleAssemblyIndes = new int[]{
        9, 4, 7, 2, 3
    };
    int[] lagIndes = new int[]{15, 13};
    double[] lagrangeShapeFuncWithNormalSort = new double[]{0, 0, 14, 0, 17, 0};

    @Test
    public void testAssembleVolume() {
        DenseMatrix expMat = new DenseMatrix(getMatrixSize(), getMatrixSize());
        DenseVector expVec = new DenseVector(getMatrixSize());
        DenseVector wholeShapeFunction = getWholeShapeFunction();
        double wholeWeight = weight * radialFunctionCore.values(load[0], null)[0];
        for (MatrixEntry me : expMat) {
            me.set(wholeShapeFunction.get(me.row())
                    * wholeShapeFunction.get(me.column())
                    * wholeWeight);
        }
        for (VectorEntry ve : expVec) {
            ve.set(wholeShapeFunction.get(ve.index())
                    * wholeWeight * load[0]);
        }

        LevelSetApproximationAssembler sampleAssembler = getSampleAssembler();
        sampleAssembler.assembleVolume();
        Matrix actMat = sampleAssembler.getMainMatrix();
        Vector actVec = sampleAssembler.getMainVector();
        for (MatrixEntry me : expMat) {
            if (sampleAssembler.isUpperSymmertric() && me.row() > me.column()) {
                continue;
            }
            double act = actMat.get(me.row(), me.column());
            double exp = me.get();
            assertEquals(exp, act, Math.abs(exp) * 1e-4);
        }

        for (VectorEntry ve : expVec) {
            double act = actVec.get(ve.index());
            double exp = ve.get();
            assertEquals(exp, act, Math.abs(exp) * 1e-4);
        }
    }

    @Test
    public void testAssembleDirichlet() {
        DenseMatrix expMat = new DenseMatrix(getMatrixSize(), getMatrixSize());
        DenseVector expVec = new DenseVector(getMatrixSize());
        DenseVector wholeShapeFunction = getWholeShapeFunction();
        DenseVector wholeLagrangeShapeFunction = getWholeLagrangeShapeFunction();


        double wholeWeight = weight * load[0];
        expVec.set(wholeLagrangeShapeFunction);
        expVec.scale(-wholeWeight);

        for (VectorEntry sve : wholeShapeFunction) {
            for (VectorEntry lve : wholeLagrangeShapeFunction) {
                double value = -sve.get() * lve.get() * weight;
                expMat.set(sve.index(), lve.index(), value);
            }
        }

        LevelSetApproximationAssembler sampleAssembler = getSampleAssembler();
        sampleAssembler.assembleDirichlet();
        Matrix actMat = sampleAssembler.getMainMatrix();
        Vector actVec = sampleAssembler.getMainVector();
        for (VectorEntry ve : expVec) {
            double act = actVec.get(ve.index());
            double exp = ve.get();
            assertEquals(exp, act, Math.abs(exp) * 1e-4);
        }
        for (MatrixEntry me : expMat) {
            if (sampleAssembler.isUpperSymmertric() && me.row() > me.column()) {
                continue;
            }
            double act = actMat.get(me.row(), me.column());
            double exp = me.get();
            assertEquals(exp, act, Math.abs(exp) * 1e-4);
        }


    }

    private DenseVector getWholeShapeFunction() {
        DenseVector result = new DenseVector(getMatrixSize());
        for (int i = 0; i < shapeFunctionWithNormalSort.length; i++) {
            result.set(i, shapeFunctionWithNormalSort[i]);
        }

        return result;
    }

    private DenseVector getWholeLagrangeShapeFunction() {
        DenseVector result = new DenseVector(getMatrixSize());
        for (int i = 0; i < lagrangeShapeFuncWithNormalSort.length; i++) {
            result.set(i + shapeFunctionWithNormalSort.length, lagrangeShapeFuncWithNormalSort[i]);
        }
        return result;
    }

    private int getMatrixSize() {
        return shapeFunctionWithNormalSort.length + lagrangeShapeFuncWithNormalSort.length;
    }

    public LevelSetApproximationAssembler getSampleAssembler() {

        int nodesNum = shapeFunctionWithNormalSort.length;
        TDoubleArrayList[] shapeFunc = new TDoubleArrayList[]{new TDoubleArrayList()};
        for (int i : sampleAssemblyIndes) {
            shapeFunc[0].add(shapeFunctionWithNormalSort[i]);
        }


        int lagNodesNum = lagrangeShapeFuncWithNormalSort.length;

        TDoubleArrayList lagFunc = new TDoubleArrayList();
        for (int i : lagIndes) {
            lagFunc.add(lagrangeShapeFuncWithNormalSort[i - shapeFunctionWithNormalSort.length]);
        }

        LevelSetApproximationAssembler assembler = new LevelSetApproximationAssembler();

        assembler.setDirichletNodesNum(lagNodesNum);
        assembler.setNodesNum(nodesNum);
        assembler.setMatrixDense(true);
        assembler.prepare();
        assembler.setWeight(weight);
        assembler.setLoad(load, loadValidity);
        assembler.setShapeFunctionValue(new TIntArrayList(sampleAssemblyIndes), shapeFunc);
        assembler.setLagrangeShapeFunctionValue(new TIntArrayList(lagIndes), lagFunc);
        assembler.setWeightFunction(radialFunctionCore);

        return assembler;
    }
}