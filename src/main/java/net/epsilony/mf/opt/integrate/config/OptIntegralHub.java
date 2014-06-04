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
package net.epsilony.mf.opt.integrate.config;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import net.epsilony.mf.opt.integrate.LevelFunctionalIntegrator;
import net.epsilony.mf.process.mix.MFMixerFunctionPack;
import net.epsilony.mf.util.bus.WeakBus;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptIntegralHub {

    private WeakBus<MFMixerFunctionPack>                        levelMixerFunctionPackBus;

    private Consumer<Object>                                    objectCalculateTrigger;
    private Consumer<double[]>                                  objectParameterConsumer;
    private DoubleSupplier                                      objectValueSupplier;
    private Supplier<double[]>                                  objectGradientSupplier;

    private Consumer<Object>                                    inequalConstraintsCalculateTrigger;
    private Consumer<double[]>                                  inequalConstraintsParameterConsumer;
    private Supplier<List<DoubleSupplier>>                      inequalConstraintsValueSuppliers;
    private Supplier<List<Supplier<double[]>>>                  inequalConstraintsGradientSuppliers;

    private Consumer<LevelFunctionalIntegrator>                 objectIntegratorConsumer;
    private Consumer<List<? extends LevelFunctionalIntegrator>> inequalConstraintsRangeIntegratorsConsumer;
    private Consumer<List<? extends LevelFunctionalIntegrator>> inequalConstraintsDomainIntegratorsConsumer;

    private WeakBus<Integer>                                    quadratureDegreeBus;
    private Consumer<Map<String, Object>>                       prepareTrigger;

    public void setLevelMixerPackFunctionProtoSupplier(
            Supplier<? extends MFMixerFunctionPack> levelMixerFunctionPackProtoSupplier) {
        levelMixerFunctionPackBus.postToEach(levelMixerFunctionPackProtoSupplier);
    }

    public Consumer<Map<String, Object>> getPrepareTrigger() {
        return prepareTrigger;
    }

    public Consumer<double[]> getObjectParameterConsumer() {
        return objectParameterConsumer;
    }

    public Consumer<Object> getObjectCalculateTrigger() {
        return objectCalculateTrigger;
    }

    public DoubleSupplier getObjectValueSupplier() {
        return objectValueSupplier;
    }

    public Supplier<double[]> getObjectGradientSupplier() {
        return objectGradientSupplier;
    }

    public Consumer<double[]> getInequalConstraintsParameterConsumer() {
        return inequalConstraintsParameterConsumer;
    }

    public Consumer<Object> getInequalConstraintsCalculateTrigger() {
        return inequalConstraintsCalculateTrigger;
    }

    public List<DoubleSupplier> getInequalConstraintsValueSuppliers() {
        return inequalConstraintsValueSuppliers.get();
    }

    public List<Supplier<double[]>> getInequalConstraintsGradientSuppliers() {
        return inequalConstraintsGradientSuppliers.get();
    }

    public void setQuadratureDegree(int degree) {
        quadratureDegreeBus.post(degree);
    }

    public void setObjectIntegrator(LevelFunctionalIntegrator integrator) {
        objectIntegratorConsumer.accept(integrator);
    }

    public void setInequalConstraintsRangeIntegrators(List<? extends LevelFunctionalIntegrator> rangeIntegrators) {
        inequalConstraintsRangeIntegratorsConsumer.accept(rangeIntegrators);
    }

    public void setInequalConstraintsDomainIntegrators(List<? extends LevelFunctionalIntegrator> domainIntegrators) {
        inequalConstraintsDomainIntegratorsConsumer.accept(domainIntegrators);
    }

    void setQuadratureDegreeBus(WeakBus<Integer> quadratureDegreeBus) {
        this.quadratureDegreeBus = quadratureDegreeBus;
    }

    void setObjectCalculateTrigger(Consumer<Object> objectCalculateTrigger) {
        this.objectCalculateTrigger = objectCalculateTrigger;
    }

    void setObjectValueSupplier(DoubleSupplier objectValueSupplier) {
        this.objectValueSupplier = objectValueSupplier;
    }

    void setObjectGradientSupplier(Supplier<double[]> objectGradientSupplier) {
        this.objectGradientSupplier = objectGradientSupplier;
    }

    void setInequalConstraintsCalculateTrigger(Consumer<Object> inequalConstraintsCalculateTrigger) {
        this.inequalConstraintsCalculateTrigger = inequalConstraintsCalculateTrigger;
    }

    void setInequalConstraintsValueSuppliersSupplier(Supplier<List<DoubleSupplier>> inequalConstraintsValueSuppliers) {
        this.inequalConstraintsValueSuppliers = inequalConstraintsValueSuppliers;
    }

    void setInequalConstraintsGradientSuppliersSupplier(
            Supplier<List<Supplier<double[]>>> inequalConstraintsGradientSuppliers) {
        this.inequalConstraintsGradientSuppliers = inequalConstraintsGradientSuppliers;
    }

    void setInequalConstraintsRangeIntegratorsConsumer(
            Consumer<List<? extends LevelFunctionalIntegrator>> rangeIntegratorsConsumer) {
        this.inequalConstraintsRangeIntegratorsConsumer = rangeIntegratorsConsumer;
    }

    void setInequalConstraintsDomainIntegratorsConsumer(
            Consumer<List<? extends LevelFunctionalIntegrator>> domainIntegratorsConsumer) {
        this.inequalConstraintsDomainIntegratorsConsumer = domainIntegratorsConsumer;
    }

    void setObjectIntegratorConsumer(Consumer<LevelFunctionalIntegrator> objectIntegratorConsumer) {
        this.objectIntegratorConsumer = objectIntegratorConsumer;
    }

    void setPrepareTriggerBus(WeakBus<Map<String, Object>> prepareTriggerBus) {
        prepareTrigger = prepareTriggerBus::post;
    }

    void setLevelMixerFunctionPackBus(WeakBus<MFMixerFunctionPack> levelMixerFunctionPackBus) {
        this.levelMixerFunctionPackBus = levelMixerFunctionPackBus;
    }

    void setObjectParameterConsumer(Consumer<double[]> objectParameterConsumer) {
        this.objectParameterConsumer = objectParameterConsumer;
    }

    void setInequalConstraintsParameterConsumer(Consumer<double[]> inequalConstraintsParameterConsumer) {
        this.inequalConstraintsParameterConsumer = inequalConstraintsParameterConsumer;
    }

}
