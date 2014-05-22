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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import net.epsilony.mf.model.MFNode;

import org.apache.commons.beanutils.BeanMap;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NodesRecorder {
    private final OptIndexialRecorder innerRecorder = new OptIndexialRecorder();
    private static final String[] properties = { "coord", "influenceRadius", "assemblyIndex", "lagrangeAssemblyIndex" };

    public void record(List<? extends MFNode> nodes) {
        BeanMap beanMap = new BeanMap();
        HashMap<String, Object> map = new HashMap<>();
        for (MFNode node : nodes) {
            beanMap.setBean(node);
            for (String property : properties) {
                Object prop = beanMap.get(property);
                map.put(property, prop);
            }
            innerRecorder.record(map);
        }
    }

    public ArrayList<MFNode> fetch() {
        DBCollection dbCollection = getDbCollection();
        DBObject query = BasicDBObjectBuilder.start(OptRecorder.UPPER_ID, innerRecorder.upperIdSupplier.get())
                .get();
        DBObject orderBy = BasicDBObjectBuilder.start(innerRecorder.getDataIndexName(), 1).get();
        DBCursor cursor = dbCollection.find(query).sort(orderBy);

        BeanMap beanMap = new BeanMap();
        ArrayList<MFNode> result = new ArrayList<>(cursor.count());
        while (cursor.hasNext()) {
            MFNode node = new MFNode();
            beanMap.setBean(node);
            DBObject next = cursor.next();
            for (String property : properties) {
                beanMap.put(property, next.get(property));
            }
            result.add(node);
        }
        return result;
    }

    public void prepareToRecord() {
        innerRecorder.prepareToRecord();
    }

    public DBCollection getDbCollection() {
        return innerRecorder.getDbCollection();
    }

    public void setDbCollection(DBCollection parametersDBCollection) {
        innerRecorder.setDbCollection(parametersDBCollection);
    }

    public void setUpperIdSupplier(Supplier<? extends ObjectId> upperIdSupplier) {
        innerRecorder.setUpperIdSupplier(upperIdSupplier);
    }

}
