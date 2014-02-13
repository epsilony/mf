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

import net.epsilony.mf.util.convertor.Convertor;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ConvertorIntegrator<IN, SUB> extends AbstractCascadeIntegrator<IN, SUB> {

    Convertor<IN, SUB> convertor;

    public ConvertorIntegrator() {
    }

    public ConvertorIntegrator(Convertor<IN, SUB> convertor) {
        this.convertor = convertor;
    }

    public Convertor<IN, SUB> getConvertor() {
        return convertor;
    }

    public void setConvertor(Convertor<IN, SUB> convertor) {
        this.convertor = convertor;
    }

    @Override
    public void integrate() {
        SUB subType = convertor.convert(unit);
        subIntegrator.setIntegrateUnit(subType);
        subIntegrator.integrate();
    }

}
