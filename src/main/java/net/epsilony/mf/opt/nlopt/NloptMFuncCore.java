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

import java.util.ArrayList;
import java.util.List;
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

    public static final Logger logger = LoggerFactory.getLogger(NloptMFuncCore.class);
    private Consumer<double[]> parameterConsumer;
    private Consumer<Object> calculateTrigger;
    private ArrayList<DoubleSupplier> resultSuppliers;
    private ArrayList<Supplier<double[]>> gradientSuppliers;

    public NloptMFuncCore() {
    }

    public void apply(double[] parameters) {
        parameterConsumer.accept(parameters);
        calculateTrigger.accept(Boolean.TRUE);
    }

    public double getResult(int index) {
        return resultSuppliers.get(index).getAsDouble();
    }

    public double[] getGradient(int index) {
        return gradientSuppliers.get(index).get();
    }

    public double[] getResults() {
        double[] results = new double[resultSuppliers.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = resultSuppliers.get(i).getAsDouble();
        }
        return results;
    }

    public double[][] getGradients() {
        double[][] gradients = new double[gradientSuppliers.size()][];
        for (int i = 0; i < gradients.length; i++) {
            gradients[i] = gradientSuppliers.get(i).get();
        }
        return gradients;
    }

    public int getResultsSize() {
        if (resultSuppliers.size() != gradientSuppliers.size()) {
            throw new IllegalArgumentException();
        }
        return resultSuppliers.size();
    }

    public void setParameterConsumer(Consumer<double[]> parameterConsumer) {
        this.parameterConsumer = parameterConsumer;
    }

    public void setCalculateTrigger(Consumer<Object> calculateTrigger) {
        this.calculateTrigger = calculateTrigger;
    }

    public void setResultSuppliers(List<? extends DoubleSupplier> resultSuppliers) {
        this.resultSuppliers = new ArrayList<>(resultSuppliers);
    }

    public void setGradientSuppliers(List<? extends Supplier<double[]>> gradientSuppliers) {
        this.gradientSuppliers = new ArrayList<>(gradientSuppliers);
    }

}
