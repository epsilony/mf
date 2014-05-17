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
package net.epsilony.mf.opt.persist.config;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import net.epsilony.mf.opt.config.OptBaseConfig;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.nlopt.InequalBiConsumer;
import net.epsilony.mf.opt.nlopt.ObjectBiConsumer;
import net.epsilony.mf.opt.persist.InequalConstraintsMongoDBRecorder;
import net.epsilony.mf.opt.persist.IntegralUnitsMongoDBRecorder;
import net.epsilony.mf.opt.persist.ObjectMongoDBRecorder;
import net.epsilony.mf.opt.persist.OptMongoDBRecorder;
import net.epsilony.mf.opt.persist.ParametersMongoDBRecorder;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class OptPersistConfig extends ApplicationContextAwareImpl {

    @Resource(name = OptBaseConfig.INIT_OPTIMIZATION_BUS)
    WeakBus<Boolean> initOptimizationBus;

    @Resource(name = OptBaseConfig.OBJECT_PARAMETERS_BUS)
    WeakBus<double[]> objectParametersBus;

    @Resource(name = OptBaseConfig.INEQUAL_CONSTRAINTS_PARAMETERS_BUS)
    WeakBus<double[]> inequalConstraintsParametersBus;

    @Bean
    public String dBName() {
        return "rs" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyww"));
    }

    @Bean
    public DB mongoDB() {
        try {
            return new MongoClient().getDB(dBName());
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public OptMongoDBRecorder optRecorder() {
        OptMongoDBRecorder result = new OptMongoDBRecorder();
        initOptimizationBus.register(OptMongoDBRecorder::record, result);
        result.setOptsDBCollection(optDBCollection());
        return result;
    }

    @Bean
    public DBCollection optDBCollection() {
        DBCollection result = mongoDB().getCollection("opt");
        return result;
    }

    @Bean
    public ParametersMongoDBRecorder objectParametersRecorder() {
        ParametersMongoDBRecorder result = new ParametersMongoDBRecorder();
        result.setDbCollection(objectParametersDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        initOptimizationBus.register(ParametersMongoDBRecorder::prepareToRecord, result);
        objectParametersBus.register(ParametersMongoDBRecorder::record, result);
        return result;
    }

    @Bean
    public DBCollection objectParametersDBCollection() {
        DBCollection result = mongoDB().getCollection("opt.obj.parameter");
        return result;
    }

    @Bean
    public ObjectMongoDBRecorder objectValueRecorder() {
        ObjectMongoDBRecorder result = new ObjectMongoDBRecorder();
        result.setDbCollection(objectValuesDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        applicationContext.getBean(ObjectBiConsumer.class).add(result::record);
        return result;
    }

    @Bean
    public DBCollection objectValuesDBCollection() {
        DBCollection result = mongoDB().getCollection("opt.obj.value");
        return result;
    }

    @Bean
    public ParametersMongoDBRecorder inequalParametersRecorder() {
        ParametersMongoDBRecorder result = new ParametersMongoDBRecorder();
        result.setDbCollection(inequalConstraintsParametersDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        initOptimizationBus.register(ParametersMongoDBRecorder::prepareToRecord, result);
        inequalConstraintsParametersBus.register(ParametersMongoDBRecorder::record, result);
        return result;
    }

    @Bean
    public DBCollection inequalConstraintsParametersDBCollection() {
        return mongoDB().getCollection("opt.ineq.parameter");
    }

    @Bean
    public InequalConstraintsMongoDBRecorder inequalConstraintsValueMongoDBRecorder() {
        InequalConstraintsMongoDBRecorder result = new InequalConstraintsMongoDBRecorder();
        result.setDbCollection(inequalConstraintsValueDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        applicationContext.getBean(InequalBiConsumer.class).add(result::record);
        return result;
    }

    @Bean
    public DBCollection inequalConstraintsValueDBCollection() {
        return mongoDB().getCollection("opt.ineq.value");
    }

    @Bean
    IntegralUnitsAspect integralUnitsAspect() {
        IntegralUnitsAspect result = new IntegralUnitsAspect();
        result.setUnitsFactory(applicationContext.getBean(TriangleMarchingIntegralUnitsFactory.class));
        result.setRecorder(integralUnitsRecorder());
        return result;
    }

    @Bean
    public IntegralUnitsMongoDBRecorder integralUnitsRecorder() {
        IntegralUnitsMongoDBRecorder recorder = new IntegralUnitsMongoDBRecorder();
        recorder.setUpperIdSupplier(optRecorder()::getCurrentId);
        recorder.setDbCollection(integralUnitsDBCollection());
        return recorder;
    }

    @Bean
    public DBCollection integralUnitsDBCollection() {
        return mongoDB().getCollection("opt.intg.unit");
    }
}
