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
package net.epsilony.mf.util.distribute;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class PropertyDistributor implements Distributor {
    ValueDistributor valueDistributor = new ValueDistributor();
    Object getter;

    @Override
    public void distribute() {
        Object value;
        try {
            value = PropertyUtils.getProperty(getter, getName());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException();
        }
        valueDistributor.setValue(value);
        valueDistributor.distribute();
    }

    public Object getGetter() {
        return getter;
    }

    public void setGetter(Object getter) {
        this.getter = getter;
    }

    public List<?> getSetters() {
        return valueDistributor.getSetters();
    }

    public void setSetters(List<?> setters) {
        valueDistributor.setSetters(setters);
    }

    public String getName() {
        return valueDistributor.getName();
    }

    public void setName(String name) {
        valueDistributor.setName(name);
    }

}
