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

package net.epsilony.mf.process.assembler;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;
import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.MatrixEntry;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Ignore
public class AssemblerTestUtils {

    public static class AssemblerTestData {

        int valueDimension;
        int spatialDimension = 2;
        int allNodesSize;
        double weight;
        double[][] testShapeFunction;
        double[][] trialShapeFunction;
        int[] assemblyIndes;
        double[] load;
        boolean[] loadValidity;
        double[][] mainVectorDifference;
        double[][] mainMatrixDifference;

    }

    public static <T> T getDataFromPythonScript(String fileName, Class<T> dataClass) {
        try {
            String pathString = AssemblerTestUtils.class.getResource(fileName).getPath();
            Process p = Runtime.getRuntime().exec("python3 " + pathString);
            Reader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            Gson gson = new Gson();
            T result = gson.fromJson(reader, dataClass);
            p.waitFor();
            if (0 != p.exitValue()) {
                throw new IllegalStateException();
            }
            return result;
        } catch (JsonIOException | JsonSyntaxException | IOException | IllegalStateException | InterruptedException ex) {
            Assert.fail("cannot get test data properly!");
        }

        return null;
    }

    public static MFMatrix genRandomMatrix(int numRows, int numCols, Random rand) {
        MFMatrix result = genTestMatrix(numRows, numCols);
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                result.set(row, col, rand.nextDouble());
            }
        }
        return result;
    }

    public static MFMatrix genTestMatrix(int numRows, int numCols) {
        MFMatrix result = MFMatries.wrap(new DenseMatrix(numRows, numCols));
        return result;
    }

    public static MFMatrix copyTestMatrix(MFMatrix mat) {
        MFMatrix result = genTestMatrix(mat.numRows(), mat.numCols());
        for (MatrixEntry me : mat) {
            result.set(me.row(), me.column(), me.get());
        }
        return result;
    }

    public static void assertMatrixByDifference(MFMatrix oriMat, MFMatrix curruntMat, MFMatrix expectDifference) {
        final double ERROR_LIMIT = 1e-14;
        if (oriMat.numCols() <= 0 || oriMat.numRows() <= 0) {
            fail("empty matries");
        }
        for (int row = 0; row < oriMat.numRows(); row++) {
            for (int col = 0; col < oriMat.numCols(); col++) {
                double ori = oriMat.get(row, col);
                double cur = curruntMat.get(row, col);
                double expDiff = null == expectDifference ? 0 : expectDifference.get(row, col);
                try {
                    assertEquals(cur - ori, expDiff, ERROR_LIMIT);
                } catch (Throwable ex) {
                    throw (ex);
                }

            }
        }
    }

    public static void setupAssembler(Assembler assembler, AssemblerTestData data) {
        assembler.setLoad(data.load, data.loadValidity);
        assembler.setWeight(data.weight);
        assembler.setNodesAssemblyIndes(new TIntArrayList(data.assemblyIndes));
        assembler.setTestShapeFunctionValues(data.testShapeFunction);
        assembler.setTrialShapeFunctionValues(data.trialShapeFunction);
    }
}
