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

import java.util.Map;

import net.epsilony.mf.util.TypeProcessorMap;
import net.epsilony.mf.util.convertor.Convertor;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TypeMapConvertorCascadeIntegrator<IN, SUB> extends AbstractCascadeIntegrator<IN, SUB> {

    TypeProcessorMap typeMap = new TypeProcessorMap();

    public static class RegistryItem<IN, SUB> {
        private final Convertor<? extends IN, ? extends SUB> onoOneConvertor;
        private final Convertor<? extends IN, ? extends Iterable<? extends SUB>> oneManyConvertor;

        public RegistryItem(Convertor<? extends IN, ? extends SUB> onoOneConvertor,
                Convertor<? extends IN, ? extends Iterable<? extends SUB>> oneManyConvertor) {
            this.onoOneConvertor = onoOneConvertor;
            this.oneManyConvertor = oneManyConvertor;
        }

        public Convertor<? extends IN, ? extends SUB> getOnoOneConvertor() {
            return onoOneConvertor;
        }

        public Convertor<? extends IN, ? extends Iterable<? extends SUB>> getOneManyConvertor() {
            return oneManyConvertor;
        }
    }

    @Override
    public void integrate() {
        Class<?> unitType = unit.getClass();
        @SuppressWarnings("unchecked")
        RegistryItem<IN, SUB> innerValue = (RegistryItem<IN, SUB>) typeMap.get(unitType);
        Convertor<? extends IN, ? extends SUB> oneOneConvertor = innerValue.getOnoOneConvertor();
        if (null != oneOneConvertor) {
            oneOneIntegrate(oneOneConvertor);
        } else {
            Convertor<? extends IN, ? extends Iterable<? extends SUB>> oneManyConvertor = innerValue
                    .getOneManyConvertor();
            oneManyIntegrate(oneManyConvertor);
        }

    }

    private void oneOneIntegrate(Convertor<? extends IN, ? extends SUB> onoOneConvertor) {
        @SuppressWarnings("rawtypes")
        Convertor convertor = onoOneConvertor;
        @SuppressWarnings("unchecked")
        SUB converted = (SUB) convertor.convert(unit);
        subIntegrator.setIntegrateUnit(converted);
        subIntegrator.integrate();
    }

    private void oneManyIntegrate(Convertor<? extends IN, ? extends Iterable<? extends SUB>> oneManyConvertor) {
        @SuppressWarnings("rawtypes")
        Convertor convertor = oneManyConvertor;
        @SuppressWarnings("unchecked")
        Iterable<? extends SUB> converted = (Iterable<? extends SUB>) convertor.convert(unit);
        for (SUB sub : converted) {
            subIntegrator.setIntegrateUnit(sub);
            subIntegrator.integrate();
        }
    }

    public void registerOneOne(Class<?> type, Convertor<? extends IN, ? extends SUB> convertor) {
        if (convertor == null) {
            throw new IllegalArgumentException();
        }
        typeMap.register(type, new RegistryItem<IN, SUB>(convertor, null));
    }

    public void registerOneMany(Class<?> type, Convertor<? extends IN, ? extends Iterable<? extends SUB>> oneMany) {
        if (oneMany == null) {
            throw new IllegalArgumentException();
        }
        typeMap.register(type, new RegistryItem<IN, SUB>(null, oneMany));
    }

    public void remove(Class<?> type) {
        typeMap.remove(type);
    }

    public void registerOneOne(Class<?>[] types, Convertor<? extends IN, ? extends SUB> convertor) {
        for (Class<?> type : types) {
            registerOneOne(type, convertor);
        }
    }

    public void registerOneMany(Class<?>[] types, Convertor<? extends IN, ? extends Iterable<? extends SUB>> oneMany) {
        for (Class<?> type : types) {
            registerOneMany(type, oneMany);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<Class<?>, RegistryItem<IN, SUB>> getRegistryCopy() {
        return (Map) typeMap.getRegistryCopy();
    }
}
