/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import net.epsilony.mf.process.IntegrateResult;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.project.MFMechanicalProject;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.util.Constants;
import no.uib.cipr.matrix.MatrixEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MatrixPersist {

    public static final Logger logger = LoggerFactory.getLogger(MatrixPersist.class);
    private final static String SQL_CREATE_MATRIES_TABLE =
            "CREATE TABLE "
            + "IF NOT EXISTS "
            + "%s "
            + "(" + Constants.SQL_DATABASE_ID_SPC + ", "
            + "num_rows INTEGER NOT NULL CHECK(num_rows>0), "
            + "num_cols INTEGER NOT NULL CHECK(num_cols>0), "
            + "entries_start_id INTEGER NOT NULL CHECK(entries_start_id>0), "
            + "entries_size INTEGER NOT NULL CHECK(entries_size>0))";
    private final static String SQL_CREATE_ENTRIES_TABLE =
            "CREATE TABLE "
            + "IF NOT EXISTS "
            + "%s "
            + "(" + Constants.SQL_DATABASE_ID_SPC + ", "
            + "row INTEGER NOT NULL CHECK(row>=0), "
            + "col INTEGER NOT NULL CHECK(col>=0), "
            + "value REAL NOT NULL)";
    Connection connection;
    private final static String SQL_INSERT_A_MATRIX =
            "INSERT INTO %s VALUES(NULL, %d, %d, %d, %d)";
    private final static String SQL_INSERT_MATRIX_ENTRIES =
            "INSERT INTO %s VALUES(NULL, ?, ?, ?)";
    private final static String DEFAULT_MATRIES_TABLE_NAME = "matries";
    private final static String DEFAULT_MATRIES_ENTRIES_NAME = "matries_entries";
    protected Statement statement;
    protected int lastMaxId;
    private String matriesTableName = DEFAULT_MATRIES_TABLE_NAME;
    private String entriesTableName = DEFAULT_MATRIES_ENTRIES_NAME;

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        statement = connection.createStatement();
    }

    public void createTables() throws SQLException {

        statement.executeUpdate(String.format(SQL_CREATE_MATRIES_TABLE, matriesTableName));
        statement.executeUpdate(String.format(SQL_CREATE_ENTRIES_TABLE, entriesTableName));
    }

    public int saveMatrix(MFMatrix mat) throws SQLException {
        logger.debug("start saving matrix: {}x{}", mat.numRows(), mat.numCols());
        int entryStartId = 1 + Persists.getMaxDbId(statement, entriesTableName);

        PreparedStatement pst = connection.prepareStatement(String.format(SQL_INSERT_MATRIX_ENTRIES, entriesTableName));
        int batchSize = 0;
        final int batchLim = Constants.SQL_BATCH_SIZE_LIMIT;
        boolean oldAutoCommit = connection.getAutoCommit();
        int entriesSize = 0;
        connection.setAutoCommit(false);
        for (MatrixEntry me : mat) {
            double value = me.get();
            if (value == 0) {
                continue;
            }

            pst.setInt(1, me.row());
            pst.setInt(2, me.column());
            pst.setDouble(3, value);
            pst.addBatch();
            if (batchSize >= batchLim) {
                pst.executeBatch();
                pst.clearBatch();
                batchSize = 0;
                connection.commit();
            }
            batchSize++;
            entriesSize++;
        }

        if (batchSize != 0) {
            pst.executeBatch();
        }
        statement.executeUpdate(String.format(
                SQL_INSERT_A_MATRIX,
                matriesTableName, mat.numRows(), mat.numCols(), entryStartId, entriesSize));
        connection.commit();
        connection.setAutoCommit(oldAutoCommit);
        lastMaxId = Persists.getMaxDbId(statement, matriesTableName);
        logger.debug("matrix saved as id:{}", lastMaxId);
        return lastMaxId;
    }

    public void setMatriesTableName(String matriesTableName) {
        this.matriesTableName = matriesTableName;
    }

    public void setEntriesTableName(String entriesTableName) {
        this.entriesTableName = entriesTableName;
    }

    public String getMatriesTableName() {
        return matriesTableName;
    }

    public String getEntriesTableName() {
        return entriesTableName;
    }

    public int getLastMaxId() {
        return lastMaxId;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/epsilon/Desktop/temp_test.db");
        MatrixPersist msql = new MatrixPersist();
        msql.setConnection(connection);
        msql.createTables();
        MFMechanicalProject project = SimpMFMechanicalProject.genTimoshenkoProjectFactory().produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.preprocess();
        IntegrateResult integrateResult = processor.getIntegrateResult();
        msql.saveMatrix(MFMatries.wrap(integrateResult.getMainMatrix()));
        processor.solve();
    }
}
