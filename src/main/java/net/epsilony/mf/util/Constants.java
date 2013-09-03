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
    public static final int SQLITE_BATCH_SIZE_LIMIT = 100_000;

    public static MFShapeFunction defaultMFShapeFunction() {
        return new MLS();
    }

    public static MFSolver defaultMFSolver() {
        return new RcmSolver();
    }

    private Constants() {
    }
}
