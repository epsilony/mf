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
import net.epsilony.mf.util.MFConstants;
import no.uib.cipr.matrix.VectorEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class VectorPersist {

    public static final Logger logger = LoggerFactory.getLogger(VectorPersist.class);
    private final static String SQL_CREATE_VECTORS_TABLE =
            "CREATE TABLE "
            + "IF NOT EXISTS "
            + "%s "
            + "(" + MFConstants.SQL_DATABASE_ID_SPC + ", "
            + "size INTEGER NOT NULL CHECK(size>0), "
            + "entries_start_id INTEGER NOT NULL CHECK(entries_start_id>0), "
            + "entries_size INTEGER NOT NULL CHECK(entries_size>0))";
    private final static String SQL_CREATE_VECTORS_ENTRIES_TABLE =
            "CREATE TABLE "
            + "IF NOT EXISTS "
            + "%s "
            + "(" + MFConstants.SQL_DATABASE_ID_SPC + ", "
            + "row INTEGER NOT NULL CHECK(row>=0), "
            + "value REAL NOT NULL)";
    Connection connection;
    private final static String SQL_INSERT_A_VECTOR =
            "INSERT INTO %s VALUES(NULL, %d, %d, %d)";
    private final static String SQL_INSERT_MATRIX_ENTRIES =
            "INSERT INTO %s VALUES(NULL, ?, ?)";
    private final static String DEFAULT_VECTORS_TABLE_NAME = "vectors";
    private final static String DEFAULT_VECTORS_ENTRIES_NAME = "vectors_entries";
    protected Statement statement;
    protected int lastVectorId;
    private String vectorsTableName = DEFAULT_VECTORS_TABLE_NAME;
    private String entriesTableName = DEFAULT_VECTORS_ENTRIES_NAME;

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        statement = connection.createStatement();
    }

    public void createTables() throws SQLException {
        statement.executeUpdate(String.format(SQL_CREATE_VECTORS_TABLE, vectorsTableName));
        statement.executeUpdate(String.format(SQL_CREATE_VECTORS_ENTRIES_TABLE, entriesTableName));
    }

    public int storeVector(MFVector vec) throws SQLException {
        logger.debug("start saving vector: {}", vec.size());
        int entryStartId = 1 + Persists.getMaxDbId(statement, entriesTableName);

        PreparedStatement pst = connection.prepareStatement(String.format(SQL_INSERT_MATRIX_ENTRIES, entriesTableName));
        int batchSize = 0;
        final int batchLim = MFConstants.SQL_BATCH_SIZE_LIMIT;
        boolean oldAutoCommit = connection.getAutoCommit();
        int entriesSize = 0;
        connection.setAutoCommit(false);
        for (VectorEntry ve : vec) {
            double value = ve.get();
            if (value == 0) {
                continue;
            }

            pst.setInt(1, ve.index());
            pst.setDouble(2, value);
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
                SQL_INSERT_A_VECTOR,
                vectorsTableName, vec.size(), entryStartId, entriesSize));
        connection.commit();
        connection.setAutoCommit(oldAutoCommit);
        lastVectorId = Persists.getMaxDbId(statement, vectorsTableName);
        logger.debug("matrix saved as id:{}", lastVectorId);
        return lastVectorId;
    }

    public void setVectorsTableName(String vectorsTableName) {
        this.vectorsTableName = vectorsTableName;
    }

    public void setEntriesTableName(String entriesTableName) {
        this.entriesTableName = entriesTableName;
    }

    public int getLastVectorId() {
        return lastVectorId;
    }

    public String getVectorsTableName() {
        return vectorsTableName;
    }

    public String getEntriesTableName() {
        return entriesTableName;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        MFMechanicalProject project = SimpMFMechanicalProject.genTimoshenkoProjectFactory().produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.preprocess();
        IntegrateResult integrateResult = processor.getIntegrateResult();

        Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/epsilon/Desktop/temp_test.db");

        MatrixPersist msql = new MatrixPersist();
        msql.setConnection(connection);
        msql.createTables();
        msql.storeMatrix(MFMatries.wrap(integrateResult.getMainMatrix()));

        VectorPersist vp = new VectorPersist();
        vp.setConnection(connection);
        vp.createTables();
        vp.storeVector(MFVectors.wrap(integrateResult.getMainVector()));

        processor.solve();
    }
}
