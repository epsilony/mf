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
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PoissonAssemblerTest {

    public PoissonAssemblerTest() {
    }

    public static class TestData {

        int dimension;
        double[][] test_shape_function;
        double[][] trial_shape_function;
        int[] nodes_assembly_indes;
        double weight;
        double[] load;
        double[][] main_matrix;
        double[] main_vector;
        int nodes_num;
        int lagrangle_nodes_num;
    }
    public static final String PYTHON_SCRIPT_NAME = "poisson_assembler.py";

    /**
     * Test of assembleVolume method, of class PoissonAssembler.
     */
    @Test
    public void testAssembleVolume() throws IOException, JsonIOException, InterruptedException, JsonSyntaxException {
        TestData[] datas = getDataFromPythonScript(PYTHON_SCRIPT_NAME);
        boolean tested = false;
        for (TestData data : datas) {
            tested = true;
            testAssembleVolumeElem(data);
        }
        assertTrue(tested);
    }

    public void testAssembleVolumeElem(TestData data) {
        PoissonAssembler pa = new PoissonAssembler();
        pa.setSpatialDimension(data.dimension);
        pa.setLagrangeNodesSize(data.lagrangle_nodes_num);
        pa.setNodesNum(data.nodes_num);
        pa.setUpperSymmetric(false);
        pa.setMatrixDense(true);
        pa.prepare();

        pa.setWeight(data.weight);
        pa.setLoad(data.load, null);
        pa.setNodesAssemblyIndes(TIntArrayList.wrap(data.nodes_assembly_indes));
        pa.setTrialShapeFunctionValues(data.trial_shape_function);
        pa.setTestShapeFunctionValues(data.test_shape_function);
        pa.assembleVolume();
        Matrix mainMatrix = pa.getMainMatrix();
        DenseVector mainVector = pa.getMainVector();

        assertEquals(data.main_matrix.length, mainMatrix.numRows());
        assertEquals(data.main_matrix[0].length, mainMatrix.numColumns());
        assertEquals(data.main_vector.length, mainVector.size());

        for (int i = 0; i < data.main_matrix.length; i++) {
            double[] row = data.main_matrix[i];
            for (int j = 0; j < row.length; j++) {
                double exp = row[j];
                if (i == j && j >= data.nodes_num) {
                    exp = 1;  // lagrangle dirichlect assembly convention for this project
                }
                assertEquals(exp, mainMatrix.get(i, j), 1e-14);
            }
        }
    }

    static TestData[] getDataFromPythonScript(String fileName) throws IOException, JsonIOException, InterruptedException, JsonSyntaxException {
        String pathString = PoissonAssemblerTest.class.getResource(fileName).getPath();
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
}