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

import net.epsilony.tb.nlopt.NloptLibrary.NloptFunc;

import org.bridj.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptFuncWrapper extends NloptFunc {

    private double[] parameters;
    private Consumer<double[]> parametersConsumer;
    private DoubleSupplier valueSupplier;
    private Supplier<double[]> gradientSupplier;

    public static Logger logger = LoggerFactory.getLogger(NloptFuncWrapper.class);

    public NloptFuncWrapper(Consumer<double[]> parametersConsumer, DoubleSupplier valueSupplier,
            Supplier<double[]> gradientSupplier) {
        this.parametersConsumer = parametersConsumer;
        this.valueSupplier = valueSupplier;
        this.gradientSupplier = gradientSupplier;
    }

    public NloptFuncWrapper() {
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

        parametersConsumer.accept(parameters);

        double result = valueSupplier.getAsDouble();
        double[] gradient = gradientSupplier.get();
        if (n != gradient.length) {
            throw new IllegalStateException();
        }
        gradOut.setDoubles(gradient);
        logger.debug("result = {}", result);
        return result;
    }

    public void setParametersConsumer(Consumer<double[]> parametersConsumer) {
        this.parametersConsumer = parametersConsumer;
    }

    public void setValueSupplier(DoubleSupplier valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    public void setGradientSupplier(Supplier<double[]> gradientSupplier) {
        this.gradientSupplier = gradientSupplier;
    }

    public double[] getParameters() {
        return parameters;
    }

    public Consumer<double[]> getParametersConsumer() {
        return parametersConsumer;
    }

    public DoubleSupplier getValueSupplier() {
        return valueSupplier;
    }

    public Supplier<double[]> getGradientSupplier() {
        return gradientSupplier;
    }

}
