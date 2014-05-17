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
public class InequalConstraintsMongoDBRecorder extends AbstractUpIndexMongoDBRecorder {
    public static final String INEQUALS_DBCOLLECTION = "opts.inequals";

    public static final String VALUES = "values";
    public static final String GRADIENTS = "gradients";

    public void record(double[] values, double[][] gradients) {
        preWriteDBObject();
        dbObject.put(VALUES, values);
        dbObject.put(GRADIENTS, gradients);

        dbCollection.insert(dbObject);
    }

}
