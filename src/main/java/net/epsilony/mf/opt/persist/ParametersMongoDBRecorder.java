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

import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import com.google.common.base.Supplier;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ParametersMongoDBRecorder {

    // recommended
    public static final String PARAMETERS_COLLECTION = "opts.parameters";
    //
    public static final String DURATION_TO_FIRST = "toFirst";
    public static final String START_TIME = "startTime";
    public static final String UPPER_ID = "upId";
    public static final String DATA = "data";
    public static final String DATA_INDEX = "dataIndex";

    private Supplier<? extends ObjectId> upperIdSupplier;
    private DBCollection parametersDBCollection;
    private int index;
    private long firstMilli;

    private final BasicDBObject record = new BasicDBObject();

    public void prepareToRecord() {
        index = 0;

        BasicDBObject options = new BasicDBObject("background", true);
        parametersDBCollection.createIndex(new BasicDBObject(UPPER_ID, -1).append(DATA_INDEX, 1), options);
    }

    public void record(double[] parameters) {
        long toFirst;
        if (index == 0) {
            firstMilli = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
            toFirst = 0;
        } else {
            toFirst = toFirstMilli();
        }
        record.put(UPPER_ID, upperIdSupplier.get());
        record.put("_id", null);
        record.put(DATA, parameters);
        record.put(DATA_INDEX, index++);

        record.put(DURATION_TO_FIRST, toFirst);

        parametersDBCollection.insert(record);
    }

    private long toFirstMilli() {
        return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) - firstMilli;
    }

    public DBCollection getParametersDBCollection() {
        return parametersDBCollection;
    }

    public void setParametersDBCollection(DBCollection parametersDBCollection) {
        this.parametersDBCollection = parametersDBCollection;
    }

    public void setUpperIdSupplier(Supplier<? extends ObjectId> upperIdSupplier) {
        this.upperIdSupplier = upperIdSupplier;
    }

    public static void main(String[] args) throws UnknownHostException {
        MongoClient client = new MongoClient();
        DB db = client.getDB("mydb");
        DBCollection collection = db.getCollection("tempRecord2");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.toInstant(ZoneOffset.UTC));
        collection.insert(new BasicDBObject().append("time", date));
        System.out.println("date = " + date);
        Instant instant = now.toInstant(ZoneOffset.UTC);

    }

}
