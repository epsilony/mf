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

package net.epsilony.mf.process.integrate.observer;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.util.AbstractObservable;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpIntegratorCoreObservable extends
        AbstractObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> implements Serializable {

    EnumMap<MFIntegratorObserverKey, Object> data = new EnumMap<>(MFIntegratorObserverKey.class);
    MFIntegratorCore core;

    public SimpIntegratorCoreObservable(MFIntegratorCore core) {
        this.core = core;
    }

    public Map<MFIntegratorObserverKey, Object> getDefaultData() {
        data.clear();
        data.put(MFIntegratorObserverKey.CORE, core);
        return data;
    }
}
