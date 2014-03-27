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
public class VarargsMethodBus extends SoftMethodRegistry implements VarargsPoster {
    public static final Class<?>[] EMPTY_TYPES = types();

    public static Class<?>[] types(Class<?>... types) {
        return types;
    }

    private Object[] values;

    @Override
    public void post(Object... values) {
        this.values = values;
        _post(false);
    }

    @Override
    public void postToFresh(Object... values) {
        this.values = values;
        _post(true);
    }

    @Override
    protected Object[] genValues() {
        return values;
    }

    public VarargsMethodBus(Class<?>... parameterTypes) {
        super(parameterTypes);
    }

}
