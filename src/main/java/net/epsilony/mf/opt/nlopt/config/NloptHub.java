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

    private final WeakBus<Map<String, Object>> prepareBus = new WeakBus<>("nloptPrepareBus");
    private final WeakBus<Consumer<double[]>> objectParameterConsumerBus = new WeakBus<>(
            "nloptObjectParameterConsumerBus");
    private final WeakBus<Consumer<Object>> objectCaculateTriggerBus = new WeakBus<>("objectCalculatorTriggerBus");
    private final WeakBus<DoubleSupplier> objectValueSupplierBus = new WeakBus<>("nloptObjectValueSupplierBus");
    private final WeakBus<Supplier<double[]>> objectGradientSupplierBus = new WeakBus<>(
            "nloptObjectGradientSupplierBus");

    private final WeakBus<Consumer<double[]>> inequalConstraintsParameterConsumerBus = new WeakBus<>(
            "nloptInequalConstraintsParameterConsumerBus");
    private final WeakBus<Consumer<Object>> inequalConstraintsCalculateTriggerBus = new WeakBus<>(
            "inequalConstraintsCalculateTriggerBus");
    private final WeakBus<List<? extends DoubleSupplier>> inequalConstraintsValueSuppliersBus = new WeakBus<>(
            "nloptInequalConstraintsValueSuppliersBus");
    private final WeakBus<List<? extends Supplier<double[]>>> inequalConstraintsGradientSuppliersBus = new WeakBus<>(
            "nloptInequalConstraintsGradientSuppliersBus");

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

    void prepare(Map<String, Object> data) {
        prepareBus.post(data);
    }

    WeakBus<Consumer<double[]>> getObjectParameterConsumerBus() {
        return objectParameterConsumerBus;
    }

    WeakBus<Consumer<Object>> getObjectCaculateTriggerBus() {
        return objectCaculateTriggerBus;
    }

    WeakBus<DoubleSupplier> getObjectValueSupplierBus() {
        return objectValueSupplierBus;
    }

    WeakBus<Supplier<double[]>> getObjectGradientSupplierBus() {
        return objectGradientSupplierBus;
    }

    WeakBus<Consumer<double[]>> getInequalConstraintsParameterConsumerBus() {
        return inequalConstraintsParameterConsumerBus;
    }

    WeakBus<Consumer<Object>> getInequalConstraintsCalculateTriggerBus() {
        return inequalConstraintsCalculateTriggerBus;
    }

    WeakBus<List<? extends DoubleSupplier>> getInequalConstraintsValueSuppliersBus() {
        return inequalConstraintsValueSuppliersBus;
    }

    WeakBus<List<? extends Supplier<double[]>>> getInequalConstraintsGradientSuppliersBus() {
        return inequalConstraintsGradientSuppliersBus;
    }

}
