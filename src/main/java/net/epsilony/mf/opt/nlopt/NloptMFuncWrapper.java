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

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.epsilony.tb.nlopt.NloptLibrary.NloptMfunc;

import org.bridj.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptMFuncWrapper extends NloptMfunc {

    private Consumer<double[]> parametersConsumer;
    private double[] parameters;
    private Supplier<double[]> resultsSupplier;
    private Supplier<double[][]> gradientsSupplier;
    public static final Logger logger = LoggerFactory.getLogger(NloptMFuncWrapper.class);

    public NloptMFuncWrapper() {
    }

    public NloptMFuncWrapper(Consumer<double[]> parametersConsumer, Supplier<double[]> resultsSupplier,
            Supplier<double[][]> gradientsSupplier) {
        this.parametersConsumer = parametersConsumer;
        this.resultsSupplier = resultsSupplier;
        this.gradientsSupplier = gradientsSupplier;
    }

    public void setParametersConsumer(Consumer<double[]> parametersConsumer) {
        this.parametersConsumer = parametersConsumer;
    }

    @Override
    public void apply(int m, Pointer<Double> resOut, int n, Pointer<Double> parIn, Pointer<Double> gradOut,
            Pointer<?> func_data) {

        if (null == parameters) {
            parameters = new double[n];
        } else if (parameters.length != n) {
            throw new IllegalStateException();
        }

        parIn.getDoublesAtOffset(0, parameters, 0, n);
        parametersConsumer.accept(parameters);

        double[][] gradients = gradientsSupplier.get();
        if (gradients.length != m) {
            throw new IllegalStateException();
        }

        for (int i = 0; i < gradients.length; i++) {
            double[] gradient = gradients[i];
            if (n != gradient.length) {
                throw new IllegalArgumentException();
            }
            gradOut.setDoublesAtOffset(Double.BYTES * i * n, gradient);
        }

        double[] results = resultsSupplier.get();
        logger.debug("results = {}", results);
        resOut.setDoubles(results);
    }

    public double[] getParameters() {
        return parameters;
    }

    public Supplier<double[]> getResultsSupplier() {
        return resultsSupplier;
    }

    public void setResultsSupplier(Supplier<double[]> resultsSupplier) {
        this.resultsSupplier = resultsSupplier;
    }

    public Supplier<double[][]> getGradientsSupplier() {
        return gradientsSupplier;
    }

    public void setGradientsSupplier(Supplier<double[][]> gradientsSupplier) {
        this.gradientsSupplier = gradientsSupplier;
    }

    public Consumer<double[]> getParametersConsumer() {
        return parametersConsumer;
    }

}
