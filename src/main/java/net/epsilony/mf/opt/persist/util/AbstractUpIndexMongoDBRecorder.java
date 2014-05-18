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
package net.epsilony.mf.opt.persist.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class AbstractUpIndexMongoDBRecorder {
    public static final String DURATION_TO_FIRST = "toFirst";
    public static final String UPPER_ID = "upId";
    public static final String DEFAULT_INDEX = "refIndex";

    protected Supplier<? extends ObjectId> upperIdSupplier;
    protected DBCollection dbCollection;
    protected int index;
    protected long firstMilli;

    protected final BasicDBObject dbObject = new BasicDBObject();

    public void prepareToRecord() {
        index = 0;

        BasicDBObject options = new BasicDBObject("background", true);
        dbCollection.createIndex(new BasicDBObject(UPPER_ID, -1).append(getDataIndexName(), 1), options);
    }

    protected void preWriteDBObject() {
        dbObject.put("_id", null);
        dbObject.put(UPPER_ID, upperIdSupplier.get());
        dbObject.put(getDataIndexName(), index++);
        dbObject.put(DURATION_TO_FIRST, toFirstMilli());
    }

    protected String getDataIndexName() {
        return DEFAULT_INDEX;
    }

    public long toFirstMilli() {
        if (index == 0) {
            firstMilli = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
            return 0;
        } else {
            return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) - firstMilli;
        }
    }

    public DBCollection getDbCollection() {
        return dbCollection;
    }

    public void setDbCollection(DBCollection parametersDBCollection) {
        this.dbCollection = parametersDBCollection;
    }

    public void setUpperIdSupplier(Supplier<? extends ObjectId> upperIdSupplier) {
        this.upperIdSupplier = upperIdSupplier;
    }

}
