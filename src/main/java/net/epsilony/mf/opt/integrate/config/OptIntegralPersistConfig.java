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

import java.util.function.Supplier;

import net.epsilony.mf.opt.persist.OptIndexialRecorder;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.mongodb.DB;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class OptIntegralPersistConfig extends ApplicationContextAwareImpl {

    @Bean
    public OptIntegralPersistHub optIntegralPersistHub() {
        return new OptIntegralPersistHub();
    }

    WeakBus<DB> getDbBus() {
        return optIntegralPersistHub().getDbBus();
    }

    WeakBus<Supplier<ObjectId>> getCurrentRootIdSupplier() {
        return optIntegralPersistHub().getCurrentRootIdSupplierBus();
    }

    @Bean
    IntegralUnitsAspect integralUnitsAspect() {
        IntegralUnitsAspect result = new IntegralUnitsAspect();
        result.setRecorder(integralUnitsRecorder());
        return result;
    }

    @Bean
    public OptIndexialRecorder integralUnitsRecorder() {
        OptIndexialRecorder recorder = new OptIndexialRecorder();
        getDbBus().register((obj, db) -> {
            obj.setDbCollection(db.getCollection("opt.intg.unit"));
        }, recorder);
        getCurrentRootIdSupplier().register(OptIndexialRecorder::setUpperIdSupplier, recorder);
        return recorder;
    }
}
