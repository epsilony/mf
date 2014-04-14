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
package net.epsilony.mf.integrate.integrator.vc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpIntegralMixRecorder {
    final ArrayList<List<IntegralMixRecordEntry>> subRecorders = new ArrayList<>();

    public List<IntegralMixRecordEntry> subRecorder() {
        LinkedList<IntegralMixRecordEntry> result = new LinkedList<IntegralMixRecordEntry>();
        subRecorders.add(result);
        return result;
    }

    public ArrayList<List<IntegralMixRecordEntry>> getSubRecorders() {
        return subRecorders;
    }

    public ArrayList<IntegralMixRecordEntry> gatherRecords() {
        int size = 0;
        for (List<?> ls : subRecorders) {
            size += ls.size();
        }

        ArrayList<IntegralMixRecordEntry> result = new ArrayList<>(size);
        for (List<IntegralMixRecordEntry> ls : subRecorders) {
            result.addAll(ls);
        }
        return result;

    }
}
