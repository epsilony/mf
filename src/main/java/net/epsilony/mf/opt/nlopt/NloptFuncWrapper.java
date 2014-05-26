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
package net.epsilony.mf.opt.nlopt;

import net.epsilony.tb.nlopt.NloptLibrary.NloptFunc;

import org.bridj.Pointer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptFuncWrapper extends NloptFunc {

    private NloptMFuncCore core;

    private double[] parameters;

    public NloptFuncWrapper() {
    }

    public NloptFuncWrapper(NloptMFuncCore core) {
        this.core = core;
    }

    @Override
    public double apply(int n, Pointer<Double> parsPt, Pointer<Double> gradOut, Pointer<?> func_data) {
        if (null == parameters) {
            parameters = new double[n];
        } else {
            if (n != parameters.length) {
                throw new IllegalArgumentException();
            }
        }
        parsPt.getDoublesAtOffset(0, parameters, 0, n);

        core.apply(parameters);

        double result = core.getResult(0);
        double[] gradient = core.getGradient(0);
        if (n != gradient.length) {
            throw new IllegalStateException();
        }
        gradOut.setDoubles(gradient);
        return result;
    }

    public void setCore(NloptMFuncCore core) {
        this.core = core;
    }

}
