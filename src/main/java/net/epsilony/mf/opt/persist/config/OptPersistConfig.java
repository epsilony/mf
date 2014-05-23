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
import net.epsilony.mf.opt.persist.NodesRecorder;
import net.epsilony.mf.opt.persist.OptIndexialRecorder;
import net.epsilony.mf.opt.persist.OptRootRecorder;
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

    public static final String OPT_RECORDER = "optRecorder";

    @Bean(name = OPT_RECORDER)
    public OptRootRecorder optRecorder() {
        OptRootRecorder result = new OptRootRecorder();
        initOptimizationBus.register(OptRootRecorder::record, result);
        result.setOptsDBCollection(optDBCollection());
        return result;
    }

    @Bean
    public NodesRecorder nodesRecorder() {
        NodesRecorder result = new NodesRecorder();
        result.setDbCollection(nodesDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        return result;
    }

    @Bean
    public DBCollection nodesDBCollection() {
        return mongoDB().getCollection("opt.node");
    }

    @Bean
    public DBCollection optDBCollection() {
        DBCollection result = mongoDB().getCollection("opt");
        return result;
    }

    @Bean
    public OptObjectAspect optObjectMongoDBAspect() {
        OptObjectAspect result = new OptObjectAspect();
        result.setParameterRecorder(objectParametersRecorder());
        result.setResultGradientRecorder(objectValueRecorder());
        return result;
    }

    @Bean
    public OptIndexialRecorder objectParametersRecorder() {
        OptIndexialRecorder result = new OptIndexialRecorder();
        result.setDbCollection(objectParametersDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        initOptimizationBus.register(OptIndexialRecorder::prepareToRecord, result);
        return result;
    }

    @Bean
    public DBCollection objectParametersDBCollection() {
        DBCollection result = mongoDB().getCollection("opt.obj.parameter");
        return result;
    }

    @Bean
    public OptIndexialRecorder objectValueRecorder() {
        OptIndexialRecorder result = new OptIndexialRecorder();
        result.setDbCollection(objectValuesDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        return result;
    }

    @Bean
    public DBCollection objectValuesDBCollection() {
        DBCollection result = mongoDB().getCollection("opt.obj.value");
        return result;
    }

    @Bean
    InequalConstraintsAspect InequalConstraintsMongoDBAspect() {
        InequalConstraintsAspect result = new InequalConstraintsAspect();
        result.setParametersRecorder(inequalParametersRecorder());
        result.setResultsGradientsRecorder(inequalConstraintsValueMongoDBRecorder());
        return result;
    }

    @Bean
    public OptIndexialRecorder inequalParametersRecorder() {
        OptIndexialRecorder result = new OptIndexialRecorder();
        result.setDbCollection(inequalConstraintsParametersDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
        initOptimizationBus.register(OptIndexialRecorder::prepareToRecord, result);
        return result;
    }

    @Bean
    public DBCollection inequalConstraintsParametersDBCollection() {
        return mongoDB().getCollection("opt.ineq.parameter");
    }

    @Bean
    public OptIndexialRecorder inequalConstraintsValueMongoDBRecorder() {
        OptIndexialRecorder result = new OptIndexialRecorder();
        result.setDbCollection(inequalConstraintsValueDBCollection());
        result.setUpperIdSupplier(optRecorder()::getCurrentId);
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
    public OptIndexialRecorder integralUnitsRecorder() {
        OptIndexialRecorder recorder = new OptIndexialRecorder();
        recorder.setUpperIdSupplier(optRecorder()::getCurrentId);
        recorder.setDbCollection(integralUnitsDBCollection());
        return recorder;
    }

    @Bean
    public DBCollection integralUnitsDBCollection() {
        return mongoDB().getCollection("opt.intg.unit");
    }
}
