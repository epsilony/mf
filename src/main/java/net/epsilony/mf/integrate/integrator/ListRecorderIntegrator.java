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

import net.epsilony.mf.util.event.EventBus;
import net.epsilony.mf.util.event.MethodEventBus;

/**
 * @author epsilon
 * 
 */
public class ListRecorderIntegrator<T> extends AbstractIntegrator<T> {
    List<T> records = new LinkedList<>();
    MethodEventBus methodEventBus = new MethodEventBus();

    @Override
    public void integrate() {
        records.add(unit);
    }

    public List<T> getRecords() {
        return records;
    }

    public void postRecords() {
        methodEventBus.post(getRecords());
    }

    public void registry(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        methodEventBus.registry(eventListener, methodName, parameterTypes);
    }

    public void registrySubEventBus(EventBus subBus) {
        methodEventBus.registrySubEventBus(subBus);
    }

    public void removeSubEventBus(EventBus subBus) {
        methodEventBus.removeSubEventBus(subBus);
    }

    public void remove(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        methodEventBus.remove(eventListener, methodName, parameterTypes);
    }
}
