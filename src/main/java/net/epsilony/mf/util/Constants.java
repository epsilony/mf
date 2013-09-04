/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Constants {

    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    public static final boolean DEFAULT_ENABLE_MULTITHREAD = true;
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;
    //KEYS:
    public static final String KEY_ENABLE_MULTI_THREAD = "KEY_ENABLE_MULTI_THREAD";
    //OTHERS:
    public static final int SQL_BATCH_SIZE_LIMIT = 100_000;
    public static final String SQL_DATABASE_ID_NAME = "db_id";
    public static final String SQL_DATABASE_ID_SPC = SQL_DATABASE_ID_NAME + " integer primary key autoincrement";
    public static final int SQL_NULL_PARENT_ID = -1;
    public static final String SQL_GET_MAX_DB_ID = 
            "SELECT " + SQL_DATABASE_ID_NAME + 
            " FROM %s ORDER BY " + SQL_DATABASE_ID_NAME + " DESC LIMIT 1";

    public static MFShapeFunction defaultMFShapeFunction() {
        return new MLS();
    }

    public static MFSolver defaultMFSolver() {
        return new RcmSolver();
    }

    private Constants() {
    }
}
