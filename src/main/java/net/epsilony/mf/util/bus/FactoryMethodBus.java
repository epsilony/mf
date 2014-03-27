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

import net.epsilony.tb.Factory;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
// DO NOT add generic type to this class because of erasure. Tried twice and
// failed thus wrote this!
public class FactoryMethodBus extends AbstractMethodBus {
    // DO NOT change this map's generic type, or you have to create an
    // nested type.
    private Factory<?> factory;

    public FactoryMethodBus(Factory<?> factory) {
        this.factory = factory;
    }

    public FactoryMethodBus() {
    }

    public Factory<?> getFactory() {
        return factory;
    }

    public void setFactory(Factory<?> factory) {
        this.factory = factory;
    }

    public void post() {
        onlyPostToNew = false;
        _post();
    }

    public void postToFresh() {
        onlyPostToNew = true;
        _post();
    }

    @Override
    protected Object[] genValues() {
        return new Object[] { factory.produce() };
    }

    @Override
    public void post(Object... values) {
        if (values.length > 0) {
            throw new IllegalArgumentException();
        }
        post();
    }

    @Override
    public void postToFresh(Object... values) {
        if (values.length > 0) {
            throw new IllegalArgumentException();
        }
        postToFresh();
    }

}
