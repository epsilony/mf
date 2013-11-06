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

package net.epsilony.mf.util;

import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFConstants {

    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;

    //OTHERS:
    public static final int SQL_BATCH_SIZE_LIMIT = 100_000;
    public static final String SQL_DATABASE_ID_NAME = "db_id";
    public static final String SQL_DATABASE_ID_SPC = SQL_DATABASE_ID_NAME + " integer primary key autoincrement";
//    public static final int SQL_NULL_PARENT_ID = -1;
    public static final String SQL_GET_MAX_DB_ID =
            "SELECT " + SQL_DATABASE_ID_NAME
            + " FROM %s ORDER BY " + SQL_DATABASE_ID_NAME + " DESC LIMIT 1";
    public static final double RECOMMANDED_DENSE_MATRIX_RATIO_LIMIT = 0.382;
//
    public static final double DEFAULT_DISTANCE_ERROR = 1E-6;

    public static MFShapeFunction defaultMFShapeFunction() {
        return new MLS();
    }

    public static MFSolver defaultMFSolver() {
        return new RcmSolver();
    }

    private MFConstants() {
    }
}
