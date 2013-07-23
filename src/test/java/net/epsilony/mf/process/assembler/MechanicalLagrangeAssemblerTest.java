/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.cons_law.RawConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.VectorEntry;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalLagrangeAssemblerTest {

    public MechanicalLagrangeAssemblerTest() {
    }

    @Before
    public void setUp() {
    }
    TDoubleArrayList[] shapeFuncVal = new TDoubleArrayList[]{new TDoubleArrayList(new double[]{-1.1, 2.01, 3.42})};
    TDoubleArrayList lagrangeShapeFuncVal = new TDoubleArrayList(new double[]{14, 50});
    TIntArrayList nodesAssemblyIndes = new TIntArrayList(new int[]{5, 2, 0});
    TIntArrayList lagrangleAssemblyIndes = new TIntArrayList(new int[]{16, 17, 12, 13});
    int nodesSize = 6;
    int lagNodesSize = 4;
    double[] dirichletVal = new double[]{3.4, -1.2};
    double weight = 0.23;

    /**
     * Test of assembleDirichlet method, of class MechanicalLagrangeAssembler.
     */
    @Test
    public void testAsmDirichlet() {
        double[][] exp_m = new double[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -171.0, -0, 0.0,
                0.0, -47.88, -0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0, -171.0, 0.0, 0.0, -0, -47.88, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -100.5, -0, 0.0, 0.0, -28.14, -0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0, -100.5, 0.0, 0.0, -0, -28.14, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 55.0, -0, 0.0, 0.0, 15.4, -0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0, 55.0, 0.0, 0.0, -0, 15.4, 0.0, 0.0},
            {-171.0, -0, 0.0, 0.0, -100.5, -0, 0.0, 0.0, 0.0, 0.0, 55.0, -0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-0, -171.0, 0.0, 0.0, -0, -100.5, 0.0, 0.0, 0.0, 0.0, -0, 55.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-47.88, -0, 0.0, 0.0, -28.14, -0, 0.0, 0.0, 0.0, 0.0, 15.4, -0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-0, -47.88, 0.0, 0.0, -0, -28.14, 0.0, 0.0, 0.0, 0.0, -0, 15.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}};


        double[] exp_v = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -170.0, 60.0, 0.0,
            0.0, -47.6, 16.8, 0.0, 0.0};

        for (boolean upperSym : new boolean[]{true, false}) {
            MechanicalLagrangeAssembler lag = new MechanicalLagrangeAssembler();
            lag.setConstitutiveLaw(new RawConstitutiveLaw(new DenseMatrix(3, 3)));
            lag.setNodesNum(nodesSize);
            lag.setMatrixDense(upperSym);
            lag.setDirichletDimensionSize(lagNodesSize * 2);
            lag.prepare();
            for (int test = 1; test <= 2; test++) {
                lag.setWeight(weight);
                lag.setShapeFunctionValue(nodesAssemblyIndes, shapeFuncVal);
                lag.setLagrangeShapeFunctionValue(lagrangleAssemblyIndes, lagrangeShapeFuncVal);
                lag.setLoad(dirichletVal, new boolean[]{true, true});
                lag.assembleDirichlet();
                Matrix mat = lag.getMainMatrix();
                DenseVector vec = lag.getMainVector();
                boolean getHere = false;
                for (MatrixEntry me : mat) {
                    double act = me.get();
                    double exp = exp_m[me.row()][me.column()] * weight * test;
                    assertEquals(exp, act, 1e-7);
                    getHere = true;
                }
                assertTrue(getHere);
                getHere = false;
                for (VectorEntry ve : vec) {
                    double act = ve.get();
                    double exp = exp_v[ve.index()] * weight * test;
                    assertEquals(exp, act, 1e-7);
                    getHere = true;
                }
                assertTrue(getHere);
            }
        }
    }
}
