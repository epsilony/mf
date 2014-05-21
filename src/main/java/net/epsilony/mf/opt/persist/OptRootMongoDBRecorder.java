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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptRootMongoDBRecorder {

    // public static final String ROOT_CLASS = "rootClass";
    public static final String START_TIME = "startTime";

    private DBCollection optsDBCollection;

    private final BasicDBObject dbObject = new BasicDBObject();
    private ObjectId currentId;

    public void record() {
        dbObject.put("_id", null);
        dbObject.put(START_TIME, now());
        optsDBCollection.insert(dbObject);
        currentId = (ObjectId) dbObject.get("_id");
    }

    public DBCollection getOptsDBCollection() {
        return optsDBCollection;
    }

    public void setOptsDBCollection(DBCollection optsDBCollection) {
        this.optsDBCollection = optsDBCollection;
    }

    public ObjectId getCurrentId() {
        return currentId;
    }

    private Date now() {
        LocalDateTime ldt = LocalDateTime.now();
        return Date.from(ldt.toInstant(ZoneOffset.UTC));
    }

}
