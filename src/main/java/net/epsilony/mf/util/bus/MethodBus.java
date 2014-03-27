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
package net.epsilony.mf.util.bus;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class MethodBus<T> extends SoftMethodRegistry implements Poster<T> {
    private T value;

    @Override
    public void postToFresh(T value) {
        this.value = value;
        _post(true);
    }

    @Override
    public void post(T value) {
        this.value = value;
        _post(false);
    }

    @Override
    protected Object[] genValues() {
        return new Object[] { value };
    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return (Class<T>) parameterTypes[0];
    }

    public MethodBus(Class<T> type) {
        super(type);
    }

}
