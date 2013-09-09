/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.epsilony.mf.util.matrix.MFMatries.wrap;
import static net.epsilony.mf.util.matrix.MFMatries.allocateEJML;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import static net.epsilony.mf.util.matrix.MFMatrixTestUtil.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatrixJDBCTest {

    public MFMatrixJDBCTest() {
    }

    @Test
    public void testStoreAndRetrive() throws SQLException, ClassNotFoundException {
        MFMatrixJDBC mp = new MFMatrixJDBC();
        Class.forName("org.sqlite.JDBC");
        mp.setConnection(DriverManager.getConnection("jdbc:sqlite::memory:"));
        mp.createTables();

        WrapperMFMatrix[] noiseSamples = genSamples(47);
        for (MFMatrix mat : noiseSamples) {
            mp.store(mat);
        }

        noiseSamples = genSamples(57);
        WrapperMFMatrix[] noiseSamples2 = genSamples(64);
        WrapperMFMatrix[] samples = genSamples(11100);

        int[] ids = new int[samples.length];
        for (int i = 0; i < samples.length; i++) {
            mp.store(noiseSamples[i]);
            ids[i] = mp.store(samples[i]);
            mp.store(noiseSamples2[i]);
        }

        noiseSamples = genSamples(117);
        for (MFMatrix mat : noiseSamples) {
            mp.store(mat);
        }

        boolean tested = false;
        for (int i = 0; i < ids.length; i++) {
            tested = true;
            int id = ids[i];
            Object expMat = samples[i].getBackend();
            MatrixInfo matInfo = mp.retrieveInfo(id);
            MFMatrix actMat = mp.retrieve(allocateEJML(matInfo), id);
            if (expMat instanceof Matrix) {
                assertMatries((Matrix) expMat, actMat);
            } else if (expMat instanceof Vector) {
                assertMatries((Vector) expMat, actMat);
            } else if (expMat instanceof Matrix64F) {
                assertMatries((Matrix64F) expMat, actMat);
            } else {
                throw new IllegalStateException();
            }
        }
        assertTrue(tested);
    }

    WrapperMFMatrix[] genSamples(int seed) {
        Random rand = new Random(seed);
        WrapperMFMatrix[] matries = new WrapperMFMatrix[]{
            wrap(new DenseMatrix(rand.nextInt(10) + 1, rand.nextInt(8) + 1)),
            wrap(new FlexCompRowMatrix(rand.nextInt(20) + 1, rand.nextInt(20) + 1)),
            wrap(new DenseMatrix64F(rand.nextInt(5) + 1, rand.nextInt(7) + 1)),
            wrap(new DenseVector(rand.nextInt(10) + 1))
        };

        for (MFMatrix mat : matries) {
            int num = rand.nextInt(mat.getNumCols() * mat.getNumRows()) + 2;
            for (int i = 0; i < num; i++) {
                int row = rand.nextInt(mat.getNumRows());
                int col = rand.nextInt(mat.getNumCols());
                double value = mat.getEntry(row, col) + rand.nextDouble();
                mat.setEntry(row, col, value);
            }
        }

        return matries;
    }
}