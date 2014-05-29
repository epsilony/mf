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
package net.epsilony.mf.opt.nlopt.config;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import net.epsilony.mf.util.bus.WeakBus;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptHub {

    private WeakBus<Map<String, Object>> prepareBus;
    private WeakBus<Consumer<double[]>> objectParameterConsumerBus;
    private WeakBus<Consumer<Object>> objectCaculateTriggerBus;
    private WeakBus<DoubleSupplier> objectValueSupplierBus;
    private WeakBus<Supplier<double[]>> objectGradientSupplierBus;
    private WeakBus<Consumer<double[]>> inequalConstraintsParameterConsumerBus;
    private WeakBus<Consumer<Object>> inequalConstraintsCalculateTriggerBus;
    private WeakBus<List<? extends DoubleSupplier>> inequalConstraintsValueSuppliersBus;
    private WeakBus<List<? extends Supplier<double[]>>> inequalConstraintsGradientSuppliersBus;

    public WeakBus<Map<String, Object>> getPrepareBus() {
        return prepareBus;
    }

    public void setObjectParameterConsumer(Consumer<double[]> objectParameterConsumer) {
        objectParameterConsumerBus.post(objectParameterConsumer);
    }

    public void setObjectCalculateTrigger(Consumer<Object> objectCalculatorTrigger) {
        objectCaculateTriggerBus.post(objectCalculatorTrigger);
    }

    public void setObjectValueSupplier(DoubleSupplier objectValueSupplier) {
        objectValueSupplierBus.post(objectValueSupplier);
    }

    public void setObjectGradientSupplier(Supplier<double[]> objectGradientSupplier) {
        objectGradientSupplierBus.post(objectGradientSupplier);
    }

    public void setInequalConstraintsParameterConsumer(Consumer<double[]> inequalConstraintsParameterConsumer) {
        inequalConstraintsParameterConsumerBus.post(inequalConstraintsParameterConsumer);
    }

    public void setInequalConstraintsCalculateTrigger(Consumer<Object> inequalCalculateTrigger) {
        inequalConstraintsCalculateTriggerBus.post(inequalCalculateTrigger);
    }

    public void setInequalConstraintsValueSuppliers(List<? extends DoubleSupplier> inequalConstraintsValueSuppliers) {
        inequalConstraintsValueSuppliersBus.post(inequalConstraintsValueSuppliers);
    }

    public void setInequalConstraintsGradientSuppliers(
            List<? extends Supplier<double[]>> inequalConstraintsGradientSuppliers) {
        inequalConstraintsGradientSuppliersBus.post(inequalConstraintsGradientSuppliers);
    }

    void setPrepareBus(WeakBus<Map<String, Object>> prepareBus) {
        this.prepareBus = prepareBus;
    }

    void setObjectParameterConsumerBus(WeakBus<Consumer<double[]>> objectParameterConsumerBus) {
        this.objectParameterConsumerBus = objectParameterConsumerBus;
    }

    void setObjectCaculateTriggerBus(WeakBus<Consumer<Object>> objectCaculateTriggerBus) {
        this.objectCaculateTriggerBus = objectCaculateTriggerBus;
    }

    void setObjectValueSupplierBus(WeakBus<DoubleSupplier> objectValueSupplierBus) {
        this.objectValueSupplierBus = objectValueSupplierBus;
    }

    void setObjectGradientSupplierBus(WeakBus<Supplier<double[]>> objectGradientSupplierBus) {
        this.objectGradientSupplierBus = objectGradientSupplierBus;
    }

    void setInequalConstraintsParameterConsumerBus(WeakBus<Consumer<double[]>> inequalConstraintsParameterConsumerBus) {
        this.inequalConstraintsParameterConsumerBus = inequalConstraintsParameterConsumerBus;
    }

    void setInequalConstraintsCalculateTriggerBus(WeakBus<Consumer<Object>> inequalConstraintsCalculateTriggerBus) {
        this.inequalConstraintsCalculateTriggerBus = inequalConstraintsCalculateTriggerBus;
    }

    void setInequalConstraintsValueSuppliersBus(
            WeakBus<List<? extends DoubleSupplier>> inequalConstraintsValueSuppliersBus) {
        this.inequalConstraintsValueSuppliersBus = inequalConstraintsValueSuppliersBus;
    }

    void setInequalConstraintsGradientSuppliersBus(
            WeakBus<List<? extends Supplier<double[]>>> inequalConstraintsGradientSuppliersBus) {
        this.inequalConstraintsGradientSuppliersBus = inequalConstraintsGradientSuppliersBus;
    }

}
