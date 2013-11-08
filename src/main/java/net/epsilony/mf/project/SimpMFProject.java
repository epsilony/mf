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

package net.epsilony.mf.project;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.util.MFKey;
import net.epsilony.mf.util.MFKeys;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFProject implements MFProject {

    Map<MFKey, Object> datas = new HashMap<>();

    public SimpMFProject() {
        datas.putAll(MFProjectKey.getDefaultSettings());
    }

    @Override
    public Map<MFKey, Object> getDatas() {
        return datas;
    }

    public Object put(MFKey key, Object value) {
        MFKeys.checkTypeAvailable(key, value);
        return datas.put(key, value);
    }

    @Override
    public Object get(MFKey key) {
        return datas.get(key);
    }

}
