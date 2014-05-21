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
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptMFuncCore {

    private Consumer<double[]> parametersConsumer;
    private double[] parameters;
    private Supplier<double[]> resultsSupplier;
    private Supplier<double[][]> gradientsSupplier;
    public static final Logger logger = LoggerFactory.getLogger(NloptMFuncCore.class);
    private double[] results;
    private double[][] gradients;

    public NloptMFuncCore() {
    }

    public NloptMFuncCore(Consumer<double[]> parametersConsumer, Supplier<double[]> resultsSupplier,
            Supplier<double[][]> gradientsSupplier) {
        this.parametersConsumer = parametersConsumer;
        this.resultsSupplier = resultsSupplier;
        this.gradientsSupplier = gradientsSupplier;
    }

    public void apply(double[] parameters) {
        this.parameters = parameters;
        parametersConsumer.accept(parameters);
        results = resultsSupplier.get();
        gradients = gradientsSupplier.get();
    }

    public double[] getParameters() {
        return parameters;
    }

    public double[] getResults() {
        return results;
    }

    public double[][] getGradients() {
        return gradients;
    }

    public void setParametersConsumer(Consumer<double[]> parametersConsumer) {
        this.parametersConsumer = parametersConsumer;
    }

    public void setResultSupplier(DoubleSupplier resultSupplier) {
        setResultsSupplier(() -> new double[] { resultSupplier.getAsDouble() });
    }

    public void setResultsSupplier(Supplier<double[]> resultsSupplier) {
        this.resultsSupplier = resultsSupplier;
    }

    public void setGradientSupplier(Supplier<double[]> gradientSupplier) {
        setGradientsSupplier(() -> new double[][] { gradientSupplier.get() });
    }

    public void setGradientsSupplier(Supplier<double[][]> gradientsSupplier) {
        this.gradientsSupplier = gradientsSupplier;
    }

}
