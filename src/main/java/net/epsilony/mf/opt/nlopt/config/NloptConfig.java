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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import net.epsilony.mf.opt.nlopt.NloptFuncWrapper;
import net.epsilony.mf.opt.nlopt.NloptMFuncCore;
import net.epsilony.mf.opt.nlopt.NloptMFuncWrapper;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.util.bus.WeakBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class NloptConfig {

    @Bean
    public NloptHub nloptHub() {
        NloptHub result = new NloptHub();
        result.setPrepareBus(prepareBus());

        result.setObjectParameterConsumerBus(objectParameterConsumerBus());
        result.setObjectCaculateTriggerBus(objectCaculateTriggerBus());
        result.setObjectValueSupplierBus(objectValueSupplierBus());
        result.setObjectGradientSupplierBus(objectGradientSupplierBus());

        result.setInequalConstraintsParameterConsumerBus(inequalConstraintsParameterConsumerBus());
        result.setInequalConstraintsCalculateTriggerBus(inequalConstraintsCalculateTriggerBus());
        result.setInequalConstraintsValueSuppliersBus(inequalConstraintsValueSuppliersBus());
        result.setInequalConstraintsGradientSuppliersBus(inequalConstraintsGradientSuppliersBus());

        return result;
    }

    @Bean
    WeakBus<Map<String, Object>> prepareBus() {
        return new WeakBus<>("nloptPrepareBus");
    }

    @Bean
    WeakBus<Consumer<double[]>> objectParameterConsumerBus() {
        return new WeakBus<>("nloptObjectParameterConsumerBus");
    }

    @Bean
    WeakBus<Consumer<Object>> objectCaculateTriggerBus() {
        return new WeakBus<>("objectCalculatorTriggerBus");
    }

    @Bean
    WeakBus<DoubleSupplier> objectValueSupplierBus() {
        return new WeakBus<>("nloptObjectValueSupplierBus");
    }

    @Bean
    WeakBus<Supplier<double[]>> objectGradientSupplierBus() {
        return new WeakBus<>("nloptObjectGradientSupplierBus");
    }

    @Bean
    WeakBus<Consumer<double[]>> inequalConstraintsParameterConsumerBus() {
        return new WeakBus<>("nloptInequalConstraintsParameterConsumerBus");
    }

    @Bean
    WeakBus<Consumer<Object>> inequalConstraintsCalculateTriggerBus() {
        return new WeakBus<>("inequalConstraintsCalculateTriggerBus");
    }

    @Bean
    WeakBus<List<? extends DoubleSupplier>> inequalConstraintsValueSuppliersBus() {
        return new WeakBus<>("nloptInequalConstraintsValueSuppliersBus");
    }

    @Bean
    WeakBus<List<? extends Supplier<double[]>>> inequalConstraintsGradientSuppliersBus() {
        return new WeakBus<>("nloptInequalConstraintsGradientSuppliersBus");
    }

    public static final String NLOPT_MMA_DRIVER = "nloptMMADriver";

    @Bean(name = NLOPT_MMA_DRIVER)
    public NloptMMADriver nloptMMADriver() {
        NloptMMADriver result = new NloptMMADriver();
        result.setObject(nloptObject());
        result.setInequalConstraints(nloptInequalConstraints());
        result.setInitOptimizationTrigger(prepareBus()::post);
        return result;
    }

    @Bean
    public NloptFuncWrapper nloptObject() {
        return new NloptFuncWrapper(nloptObjectCore());
    }

    @Bean
    public NloptMFuncWrapper nloptInequalConstraints() {
        return new NloptMFuncWrapper(nloptInequalCore());
    }

    public static final String NLOPT_OBJECT_CORE = "nloptObjectCore";

    @Bean(name = NLOPT_OBJECT_CORE)
    public NloptMFuncCore nloptObjectCore() {
        NloptMFuncCore result = new NloptMFuncCore();
        objectParameterConsumerBus().register(NloptMFuncCore::setParameterConsumer, result);
        objectCaculateTriggerBus().register(NloptMFuncCore::setCalculateTrigger, result);
        objectValueSupplierBus().register((obj, supplier) -> {
            obj.setResultSuppliers(Arrays.asList(supplier));
        }, result);
        objectGradientSupplierBus().register((obj, supplier) -> {
            obj.setGradientSuppliers(Arrays.asList(supplier));
        }, result);
        return result;
    }

    public static final String NLOPT_INEQUAL_CONSTRAINTS_CORE = "nloptInequalConstraintsCore";

    @Bean(name = NLOPT_INEQUAL_CONSTRAINTS_CORE)
    public NloptMFuncCore nloptInequalCore() {
        NloptMFuncCore result = new NloptMFuncCore();
        inequalConstraintsParameterConsumerBus().register(NloptMFuncCore::setParameterConsumer, result);
        inequalConstraintsCalculateTriggerBus().register(NloptMFuncCore::setCalculateTrigger, result);
        inequalConstraintsValueSuppliersBus().register(NloptMFuncCore::setResultSuppliers, result);
        inequalConstraintsGradientSuppliersBus().register(NloptMFuncCore::setGradientSuppliers, result);
        return result;
    }
}
