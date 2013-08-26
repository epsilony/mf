/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import com.google.gson.JsonIOException;
import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import net.epsilony.mf.process.assembler.MechanicalPenaltyAssemblerTest.TestData;
import net.epsilony.mf.process.assembler.MechanicalPenaltyAssemblerTest.TestDataElement;
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
public class MechanicalLagrangeAssemblerTest {

    public MechanicalLagrangeAssemblerTest() {
    }

    TestData[] getDataFromPythonScript() throws IOException, JsonIOException, IllegalStateException, InterruptedException {
        String scriptName = "mechanical_lagrange_assemblier.py";
        return MechanicalPenaltyAssemblerTest.getDataFromPythonScript(scriptName);
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

        MechanicalLagrangeAssembler mla = new MechanicalLagrangeAssembler();
        TestDataElement volElem = testData.data[0];
        if (!volElem.method.equalsIgnoreCase("volume")) {
            throw new IllegalStateException();
        }

        mla.setConstitutiveLaw(volElem.getConstitutiveLaw());
        mla.setNodesNum(volElem.nodesSize);
        mla.setDirichletNodesSize(testData.data[1].lagNodesSize);
        mla.upperSymmetric = false;
        mla.dense = true;
        mla.prepare();
        int order = 0;
        for (TestDataElement element : testData.data) {
            if (element.testOrder != order) {
                throw new IllegalArgumentException();
            }
            System.out.println("method = " + element.method);
            for (int elemIndex = 0; elemIndex < element.weights.length; elemIndex++) {
                System.out.println("i = " + elemIndex);
                mla.setWeight(element.weights[elemIndex]);
                mla.setNodesAssemblyIndes(new TIntArrayList(element.nodesAssemblyIndesArray[elemIndex]));
                if (!element.method.equals("neumann")) {
                    mla.setTrialShapeFunctionValues(
                            element.trialShapeFuncValuesArray[elemIndex]);
                }
                mla.setTestShapeFunctionValues(
                        element.testShapeFuncValuesArray[elemIndex]);
                mla.setLoad(element.loads[elemIndex], new boolean[]{true, true});
                switch (element.method) {
                    case "volume":
                        mla.assembleVolume();
                        break;
                    case "neumann":
                        mla.assembleNeumann();
                        break;
                    case "dirichlet":
                        TIntArrayList lagAssemblyIndes = new TIntArrayList(element.lagAssemblyIndesArray[elemIndex]);
                        mla.setLagrangeShapeFunctionValue(lagAssemblyIndes, element.lagShapeFuncValuesArray[elemIndex][0]);
                        mla.assembleDirichlet();
                }
                if (element.assembledMatries != null) {
                    Matrix mainMat = mla.getMainMatrix();
                    for (int row = 0; row < element.assembledMatries[elemIndex].length; row++) {
                        for (int col = 0; col < element.assembledMatries[elemIndex][row].length; col++) {
                            assertEquals(element.assembledMatries[elemIndex][row][col], mainMat.get(row, col), errLimit);
                        }
                    }
                }
                DenseVector mainVector = mla.getMainVector();
                for (int row = 0; row < element.assembledVectors[elemIndex].length; row++) {
                    assertEquals(element.assembledVectors[elemIndex][row], mainVector.get(row), errLimit);
                }
            }
            order++;
        }
    }
}
