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
package net.epsilony.mf.integrate.integrator;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.epsilony.mf.util.bus.MethodBus;
import net.epsilony.mf.util.bus.VarargsPoster;

/**
 * @author epsilon
 * 
 */
public class ListRecorderIntegrator<T> implements Consumer<T> {
    List<T> records = new LinkedList<>();
    MethodBus methodEventBus = new MethodBus();

    @Override
    public void accept(T unit) {
        records.add(unit);
    }

    public List<T> getRecords() {
        return records;
    }

    public void postRecords() {
        methodEventBus.post(getRecords());
    }

    public void register(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        methodEventBus.register(eventListener, methodName, parameterTypes);
    }

    public void registerSubEventBus(VarargsPoster subBus) {
        methodEventBus.registerSubEventBus(subBus);
    }

    public void removeSubEventBus(VarargsPoster subBus) {
        methodEventBus.removeSubEventBus(subBus);
    }

    public void remove(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        methodEventBus.remove(eventListener, methodName, parameterTypes);
    }
}
