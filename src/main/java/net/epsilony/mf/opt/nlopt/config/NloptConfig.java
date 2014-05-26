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

import net.epsilony.mf.opt.nlopt.NloptFuncWrapper;
import net.epsilony.mf.opt.nlopt.NloptMFuncCore;
import net.epsilony.mf.opt.nlopt.NloptMFuncWrapper;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;

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
        return new NloptHub();
    }

    public static final String NLOPT_MMA_DRIVER = "nloptMMADriver";

    @Bean(name = NLOPT_MMA_DRIVER)
    public NloptMMADriver nloptMMADriver() {
        NloptMMADriver result = new NloptMMADriver();
        result.setObject(nloptObject());
        result.setInequalConstraints(nloptInequalConstraints());
        result.setInitOptimizationTrigger(nloptHub()::prepare);
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
        NloptHub nloptHub = nloptHub();
        nloptHub.getObjectParameterConsumerBus().register(NloptMFuncCore::setParameterConsumer, result);
        nloptHub.getObjectCaculateTriggerBus().register(NloptMFuncCore::setCalculateTrigger, result);
        nloptHub.getObjectValueSupplierBus().register((obj, supplier) -> {
            obj.setResultSuppliers(Arrays.asList(supplier));
        }, result);
        nloptHub.getObjectGradientSupplierBus().register((obj, supplier) -> {
            obj.setGradientSuppliers(Arrays.asList(supplier));
        }, result);
        return result;
    }

    public static final String NLOPT_INEQUAL_CONSTRAINTS_CORE = "nloptInequalConstraintsCore";

    @Bean(name = NLOPT_INEQUAL_CONSTRAINTS_CORE)
    public NloptMFuncCore nloptInequalCore() {
        NloptMFuncCore result = new NloptMFuncCore();
        NloptHub nloptHub = nloptHub();
        nloptHub.getInequalConstraintsParameterConsumerBus().register(NloptMFuncCore::setParameterConsumer, result);
        nloptHub.getInequalConstraintsCalculateTriggerBus().register(NloptMFuncCore::setCalculateTrigger, result);
        nloptHub.getInequalConstraintsValueSuppliersBus().register(NloptMFuncCore::setResultSuppliers, result);
        nloptHub.getInequalConstraintsGradientSuppliersBus().register(NloptMFuncCore::setGradientSuppliers, result);
        return result;
    }
}
