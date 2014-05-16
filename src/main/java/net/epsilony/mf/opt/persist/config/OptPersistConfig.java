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

import javax.annotation.Resource;

import net.epsilony.mf.opt.config.OptBaseConfig;
import net.epsilony.mf.opt.persist.OptMongoDBRecorder;
import net.epsilony.mf.opt.persist.ParametersMongoDBRecorder;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class OptPersistConfig extends ApplicationContextAwareImpl {

    @Resource(name = OptBaseConfig.INIT_OPTIMIZATION_BUS)
    WeakBus<Boolean> initOptimizationBus;

    @Resource(name = OptBaseConfig.LEVEL_PARAMETERS_BUS)
    public WeakBus<double[]> levelParametersBus;

    @Bean
    public String dBName() {
        return "rs5016";
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
    public ParametersMongoDBRecorder optParametersRecorder() {
        ParametersMongoDBRecorder result = new ParametersMongoDBRecorder();
        result.setParametersDBCollection(optParametersDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        levelParametersBus.register(ParametersMongoDBRecorder::record, result);
        return result;
    }

    @Bean
    public DBCollection optParametersDBCollection() {
        DBCollection result = mongoDB().getCollection(ParametersMongoDBRecorder.PARAMETERS_COLLECTION);
        return result;
    }

    @Bean
    public OptMongoDBRecorder optRecorder() {
        OptMongoDBRecorder result = new OptMongoDBRecorder();
        result.setOptsDBCollection(optsDBCollection());
        return result;
    }

    @Bean
    public DBCollection optsDBCollection() {
        DBCollection result = mongoDB().getCollection(OptMongoDBRecorder.OPTS_DBCOLLECTION);
        return result;
    }

    @Bean
    public boolean phonyRegisterRecordersToInitOptimizationBus() {
        // the sort is important
        initOptimizationBus.register(OptMongoDBRecorder::record, optRecorder());
        initOptimizationBus.register(ParametersMongoDBRecorder::prepareToRecords, optParametersRecorder());
        return true;
    }

}
