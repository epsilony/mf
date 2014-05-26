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

import javax.annotation.Resource;

import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.persist.OptIndexialRecorder;
import net.epsilony.mf.opt.persist.OptRootRecorder;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class OptIntegralPersistConfig extends ApplicationContextAwareImpl {

    @Resource
    DB mongoDB;

    @Resource
    OptRootRecorder optRecorder;

    @Bean
    IntegralUnitsAspect integralUnitsAspect() {
        IntegralUnitsAspect result = new IntegralUnitsAspect();
        result.setUnitsFactory(applicationContext.getBean(TriangleMarchingIntegralUnitsFactory.class));
        result.setRecorder(integralUnitsRecorder());
        return result;
    }

    @Bean
    public OptIndexialRecorder integralUnitsRecorder() {
        OptIndexialRecorder recorder = new OptIndexialRecorder();
        recorder.setUpperIdSupplier(optRecorder::getCurrentId);
        recorder.setDbCollection(integralUnitsDBCollection());
        return recorder;
    }

    @Bean
    public DBCollection integralUnitsDBCollection() {
        return mongoDB.getCollection("opt.intg.unit");
    }
}
