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
package net.epsilony.mf.opt.persist;

import net.epsilony.mf.opt.persist.util.AbstractUpIndexMongoDBRecorder;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptObjectMongoDBRecorder extends AbstractUpIndexMongoDBRecorder {

    //
    public static final String OPT_VALUES_DBCOLLECTION = "opts.values";
    //
    public static final String UP_ID = "upId";
    public static final String VALUE = "pars";
    public static final String GRADIENT = "gradient";
    public static final String DURATION_TO_FIRST = "toFirst";

    public void record(double value, double[] gradient) {
        preWriteDBObject();
        dbObject.put(VALUE, value);
        dbObject.put(GRADIENT, gradient);

        dbCollection.insert(dbObject);
    }
}
