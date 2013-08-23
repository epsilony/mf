/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.RawConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.VectorEntry;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPenaltyAssemblerTest {

    public MechanicalPenaltyAssemblerTest() {
    }

    static class TestDataElement {

        int dim;
        double[][] constitutiveLaw;
        int nodesSize;
        double[] weights;
        int lagNodesSize;
        double[][][] testShapeFuncValuesArray;
        int[][] testAssemblyIndesArray;
        int[][] lagAssemblyIndesArray;
        double[][][] trialShapeFuncValuesArray;
        double[][][] lagShapeFuncValuesArray;
        int[][] trialAssemblyIndesArray;
        double[][] assembledVectors;
        double[][][] assembledMatries;
        double penalty;
        String method;
        int testOrder;
        double[][] loads;

        public ConstitutiveLaw getConstitutiveLaw() {
            return new RawConstitutiveLaw(new DenseMatrix(constitutiveLaw));
        }
    }

    static class TestData {

        int dim;
        TestDataElement[] data;
    }

    static TestData[] getDataFromPythonScript(String fileName) throws IOException, JsonIOException, IllegalStateException, InterruptedException, JsonSyntaxException {
        String pathString = MechanicalPenaltyAssemblerTest.class.getResource(fileName).getPath();
        Process p = Runtime.getRuntime().exec("python3 " + pathString);
        Reader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        Gson gson = new Gson();
        TestData[] result = gson.fromJson(reader, TestData[].class);
        p.waitFor();
        if (0 != p.exitValue()) {
            throw new IllegalStateException();
        }
        return result;
    }

    private TestData[] getDataFromPythonScript() throws IOException, InterruptedException {
        String fileName = "mechanical_penalty_assemblier.py";
        return getDataFromPythonScript(fileName);
    }

    @Test
    public void test2D() throws IOException, InterruptedException {
        TestData[] datas = getDataFromPythonScript();
        TestData testData = null;
        final double errLimit = 1e-10;
        for (TestData d : datas) {
            if (d.dim == 2) {
                testData = d;
                break;
            }
        }

        MechanicalPenaltyAssembler mpa = new MechanicalPenaltyAssembler();
        TestDataElement volElem = testData.data[0];
        if (!volElem.method.equalsIgnoreCase("volume")) {
            throw new IllegalStateException();
        }

        mpa.setConstitutiveLaw(volElem.getConstitutiveLaw());
        mpa.setNodesNum(volElem.nodesSize);
        mpa.setPenalty(testData.data[1].penalty);
        mpa.upperSymmetric = false;
        mpa.dense = true;
        mpa.prepare();
        int order = 0;
        for (TestDataElement element : testData.data) {
            if (element.testOrder != order) {
                throw new IllegalArgumentException();
            }
            System.out.println("method = " + element.method);
            for (int i = 0; i < element.weights.length; i++) {
                System.out.println("i = " + i);
                mpa.setWeight(element.weights[i]);
                if (element.trialAssemblyIndesArray != null) {
                    mpa.setTrialShapeFunctionValues(
                            new TIntArrayList(element.trialAssemblyIndesArray[i]),
                            element.trialShapeFuncValuesArray[i]);
                }
                mpa.setTestShapeFunctionValues(
                        new TIntArrayList(element.testAssemblyIndesArray[i]),
                        element.testShapeFuncValuesArray[i]);
                mpa.setLoad(element.loads[i], new boolean[]{true, true});
                switch (element.method) {
                    case "volume":
                        mpa.assembleVolume();
                        break;
                    case "neumann":
                        mpa.assembleNeumann();
                        break;
                    case "dirichlet":
                        mpa.assembleDirichlet();
                }
                if (element.assembledMatries != null) {
                    Matrix mainMat = mpa.getMainMatrix();

                    for (MatrixEntry me : mainMat) {
                        assertEquals(
                                element.assembledMatries[i][ me.row()][ me.column()],
                                me.get(), errLimit);
                    }
                }
                DenseVector mainVector = mpa.getMainVector();
                for (VectorEntry ve : mainVector) {
                    assertEquals(element.assembledVectors[i][ve.index()], ve.get(), errLimit);
                }
            }
            order++;
        }
    }
}