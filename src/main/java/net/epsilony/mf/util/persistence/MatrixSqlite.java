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
public class MatrixSqlite {

    public static final Logger logger = LoggerFactory.getLogger(MatrixSqlite.class);
    String matriesTableName = "matries";
    String entityTableName = "entries";
    private final String matriesTableCreate =
            "create table "
            + "if not exists "
            + "%s "
            + "(id integer primary key autoincrement, "
            + "numRows int check(numRows>0), "
            + "numCols int check(numCols>0))";
    private final String entityTableCreate =
            "create table "
            + "if not exists "
            + "%s "
            + "(id int, "
            + "row int check(row>=0), "
            + "col int check(col>=0), "
            + "value real)";
    Connection connection;
    private final String getMaxMatrixId =
            "select id from %s order by id desc limit 1";
    private final String insertAMatrix =
            "insert into %s (numRows,numCols) values(%d,%d)";
    private final String insertMatrixEntries =
            "insert into %s values(?,?,?,?)";
    protected Statement statement;

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        statement = connection.createStatement();
    }

    public void createTables() throws SQLException {

        statement.executeUpdate(String.format(matriesTableCreate, matriesTableName));
        statement.executeUpdate(String.format(entityTableCreate, entityTableName));
    }

    private int getMaxId() throws SQLException {
        return statement.executeQuery(String.format(getMaxMatrixId, matriesTableName)).getInt(1);
    }

    public void saveMatrix(MFMatrix mat) throws SQLException {
        logger.debug("start saving matrix: {}x{}", mat.numRows(), mat.numCols());
        statement.executeUpdate(String.format(insertAMatrix, matriesTableName, mat.numRows(), mat.numCols()));
        int matId = getMaxId();
        PreparedStatement pst = connection.prepareStatement(String.format(insertMatrixEntries, entityTableName));
        int batchSize = 0;
        final int batchLim = Constants.SQLITE_BATCH_SIZE_LIMIT;
        boolean oldSetting = connection.getAutoCommit();

        connection.setAutoCommit(false);
        for (MatrixEntry me : mat) {
            double value = me.get();
            if (value == 0) {
                continue;
            }
            pst.setInt(1, matId);
            pst.setInt(2, me.row());
            pst.setInt(3, me.column());
            pst.setDouble(4, value);
            pst.addBatch();
            if (batchSize >= batchLim) {
                pst.executeBatch();
                pst.clearBatch();
                batchSize = 0;
                connection.commit();
            }
            batchSize++;
        }

        if (batchSize != 0) {
            pst.executeBatch();
            connection.commit();
        }

        connection.setAutoCommit(oldSetting);
        logger.debug("matrix saved as id:{}", matId);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/epsilon/Desktop/temp_test.db");
        MatrixSqlite msql = new MatrixSqlite();
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
