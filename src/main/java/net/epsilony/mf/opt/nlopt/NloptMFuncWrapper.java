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

import net.epsilony.tb.nlopt.NloptLibrary.NloptMfunc;

import org.bridj.Pointer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptMFuncWrapper extends NloptMfunc {

    private NloptMFuncCore core;

    public NloptMFuncWrapper() {
    }

    public NloptMFuncWrapper(NloptMFuncCore core) {
        this.core = core;
    }

    private double[] parameters;

    @Override
    public void apply(int m, Pointer<Double> resOut, int n, Pointer<Double> parIn, Pointer<Double> gradOut,
            Pointer<?> func_data) {

        if (null == parameters) {
            parameters = new double[n];
        } else if (parameters.length != n) {
            throw new IllegalStateException();
        }

        parIn.getDoublesAtOffset(0, parameters, 0, n);

        core.apply(parameters);

        if (core.getResultsSize() != m) {
            throw new IllegalStateException();
        }

        for (int i = 0; i < core.getResultsSize(); i++) {
            double[] gradient = core.getGradient(i);
            if (n != gradient.length) {
                throw new IllegalArgumentException();
            }
            gradOut.setDoublesAtOffset(Double.BYTES * i * n, gradient);
        }

        double[] results = core.getResults();
        resOut.setDoubles(results);
    }

    public double[] getParameters() {
        return parameters;
    }

    public void setCore(NloptMFuncCore core) {
        this.core = core;
    }

    public int getConstraintsSize() {
        return core.getResultsSize();
    }

}
