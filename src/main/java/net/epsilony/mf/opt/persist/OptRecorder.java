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

import java.util.Map;
import java.util.function.Supplier;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptRecorder {

    public static final String UPPER_ID = "upId";
    protected Supplier<? extends ObjectId> upperIdSupplier;
    protected DBCollection dbCollection;
    protected final BasicDBObject dbObject = new BasicDBObject();

    public void record(String name, Object value) {
        preWriteDBObject();
        dbObject.put(name, value);

        dbCollection.insert(dbObject);
    }

    public void record(Map<String, ? extends Object> dataMap) {
        preWriteDBObject();
        dbObject.putAll(dataMap);

        dbCollection.insert(dbObject);
    }

    public void prepareToRecord() {
        BasicDBObject options = new BasicDBObject("background", true);
        dbCollection.createIndex(new BasicDBObject(UPPER_ID, -1), options);
    }

    protected void preWriteDBObject() {
        dbObject.clear();
        dbObject.put(UPPER_ID, upperIdSupplier.get());
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