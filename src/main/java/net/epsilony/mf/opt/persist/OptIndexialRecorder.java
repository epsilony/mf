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
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptIndexialRecorder extends OptRecorder {
    public static final String DURATION_TO_FIRST = "toFirst";
    public static final String DEFAULT_INDEX = "refIndex";

    protected int index = -1;
    protected long firstMilli;
    protected String indexName = DEFAULT_INDEX;

    @Override
    protected void _reset() {
        index = -1;
        BasicDBObject options = new BasicDBObject("background", true);
        dbCollection.createIndex(new BasicDBObject(UPPER_ID, -1).append(getDataIndexName(), 1), options);
        dbObject.clear();
    }

    @Override
    protected void preWriteDBObject() {
        super.preWriteDBObject();
        dbObject.put(getDataIndexName(), ++index);
        dbObject.put(DURATION_TO_FIRST, toFirstMilli());
    }

    @Override
    public void update(String name, Object value) {
        ObjectId currentId = getCurrentId();
        dbObject.put("lastUpdate", toFirstMilli());
        dbObject.put(name, value);
        dbCollection.update(new BasicDBObject("_id", currentId), dbObject);
    }

    @Override
    public void update(Map<String, Object> valueMap) {
        ObjectId currentId = getCurrentId();
        dbObject.put("lastUpdate", toFirstMilli());
        dbObject.putAll(valueMap);
        dbCollection.update(new BasicDBObject("_id", currentId), dbObject);
    }

    public String getDataIndexName() {
        return indexName;
    }

    public String getIndexName() {
        return indexName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public long toFirstMilli() {
        if (index == 0) {
            firstMilli = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
            return 0;
        } else {
            return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) - firstMilli;
        }
    }
}
