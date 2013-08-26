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
        int[][] nodesAssemblyIndesArray;
        int[][] lagAssemblyIndesArray;
        double[][][] trialShapeFuncValuesArray;
        double[][][] lagShapeFuncValuesArray;
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
            for (int elemIndex = 0; elemIndex < element.weights.length; elemIndex++) {
                System.out.println("i = " + elemIndex);
                mpa.setWeight(element.weights[elemIndex]);
                mpa.setNodesAssemblyIndes(new TIntArrayList(element.nodesAssemblyIndesArray[elemIndex]));

                mpa.setTestShapeFunctionValues(
                        element.testShapeFuncValuesArray[elemIndex]);
                if (!element.method.equals("neumann")) {
                    mpa.setTrialShapeFunctionValues(element.trialShapeFuncValuesArray[elemIndex]);
                }
                mpa.setLoad(element.loads[elemIndex], new boolean[]{true, true});
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

                    for (int row = 0; row < element.assembledMatries[elemIndex].length; row++) {
                        for (int col = 0; col < element.assembledMatries[elemIndex][row].length; col++) {
                            assertEquals(
                                    element.assembledMatries[elemIndex][row][ col],
                                    mainMat.get(row, col), errLimit);
                        }
                    }
                }
                DenseVector mainVector = mpa.getMainVector();
                assertEquals(element.assembledVectors[elemIndex].length, mainVector.size());
                for (VectorEntry ve : mainVector) {
                    assertEquals(element.assembledVectors[elemIndex][ve.index()], ve.get(), errLimit);
                }
            }
            order++;
        }
    }
}