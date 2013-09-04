/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.epsilony.mf.util.MFConstants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Persists {

    public static int getMaxDbId(Statement stat, String tableName) throws SQLException {
        ResultSet result = stat.executeQuery(String.format(MFConstants.SQL_GET_MAX_DB_ID, tableName));
        if (!result.isBeforeFirst()) {
            return 0;
        } else {
            return result.getInt(1);
        }
    }
}
