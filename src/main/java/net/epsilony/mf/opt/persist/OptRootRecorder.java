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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptRootRecorder {

    public static final Logger logger = LoggerFactory.getLogger(OptRootRecorder.class);
    public static final String START_TIME = "startTime";

    private DBCollection optsDBCollection;

    private final BasicDBObject dbObject = new BasicDBObject();

    public void record() {
        dbObject.put("_id", null);
        dbObject.put(START_TIME, now());
        dbObject.put("commit", getCurrentCommit());
        optsDBCollection.insert(dbObject);
    }

    public void update(Map<String, Object> valueMap) {
        ObjectId currentId = getCurrentId();
        dbObject.put("lastUpdate", now());
        dbObject.putAll(valueMap);
        optsDBCollection.update(new BasicDBObject("_id", currentId), dbObject);
    }

    public void update(String name, Object value) {
        ObjectId currentId = getCurrentId();
        dbObject.put("lastUpdate", now());
        dbObject.put(name, value);
        optsDBCollection.update(new BasicDBObject("_id", currentId), dbObject);
    }

    public Object prepareRecord(String key, Object val) {
        return dbObject.put(key, val);
    }

    public void prepareRecord(Map<String, Object> m) {
        dbObject.putAll(m);
    }

    public void clear() {
        dbObject.clear();
    }

    public DBCollection getOptsDBCollection() {
        return optsDBCollection;
    }

    public void setOptsDBCollection(DBCollection optsDBCollection) {
        this.optsDBCollection = optsDBCollection;
    }

    public ObjectId getCurrentId() {
        return dbObject.getObjectId("_id");
    }

    private Date now() {
        LocalDateTime ldt = LocalDateTime.now();
        return Date.from(ldt.toInstant(ZoneOffset.UTC));
    }

    private String getCurrentCommit() {
        ProcessBuilder pb = new ProcessBuilder("git", "rev-parse", "HEAD");
        Process start;
        try {
            start = pb.start();
            start.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(start.getInputStream()));
            return reader.readLine();
        } catch (IOException | InterruptedException e) {
            logger.warn("cannot fetch commit ver");
        }
        return null;
    }
}
