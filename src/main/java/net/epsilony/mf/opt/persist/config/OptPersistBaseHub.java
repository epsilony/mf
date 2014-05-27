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
package net.epsilony.mf.opt.persist.config;

import java.util.function.Supplier;

import net.epsilony.mf.opt.persist.OptRootRecorder;

import org.bson.types.ObjectId;

import com.mongodb.DB;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptPersistBaseHub {

    private DB db;

    private OptRootRecorder optRootRecorder;

    private Supplier<ObjectId> currentRootIdSupplier;

    public DB getDb() {
        return db;
    }

    public OptRootRecorder getOptRootRecorder() {
        return optRootRecorder;
    }

    public Supplier<ObjectId> getCurrentRootIdSupplier() {
        return currentRootIdSupplier;
    }

    void setDb(DB db) {
        this.db = db;
    }

    void setOptRootRecorder(OptRootRecorder optRootRecorder) {
        this.optRootRecorder = optRootRecorder;
        currentRootIdSupplier = optRootRecorder::getCurrentId;
    }

}
