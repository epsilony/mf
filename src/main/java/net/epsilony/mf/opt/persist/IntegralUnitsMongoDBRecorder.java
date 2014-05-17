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

import java.util.List;
import java.util.stream.Collectors;

import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.opt.persist.util.AbstractUpIndexMongoDBRecorder;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class IntegralUnitsMongoDBRecorder extends AbstractUpIndexMongoDBRecorder {
    public static final String VERTES = "vertes";

    public void record(List<? extends MFLine> chainHeads) {

        List<List<double[]>> vertes = chainHeads.stream().map(line -> {
            return line.stream().map(MFLine::getStartCoord).collect(Collectors.toList());
        }).collect(Collectors.toList());
        preWriteDBObject();
        dbObject.put(VERTES, vertes);
        dbCollection.insert(dbObject);
    }

}
