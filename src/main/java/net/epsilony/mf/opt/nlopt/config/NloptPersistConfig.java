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

import net.epsilony.mf.opt.persist.OptIndexialRecorder;
import net.epsilony.mf.opt.persist.OptRootRecorder;
import net.epsilony.mf.util.bus.WeakBus;

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
public class NloptPersistConfig {

    @Bean
    NloptPersistHub nloptPersistHub() {
        return new NloptPersistHub();
    }

    private WeakBus<DB> getDBBus() {
        return nloptPersistHub().getDbBus();
    }

    private WeakBus<OptRootRecorder> getOptRootRecorderBus() {
        return nloptPersistHub().getOptRootRecorderBus();
    }

    @Bean
    public NloptMMADriverAspect nloptMMADriverAspect() {
        NloptMMADriverAspect result = new NloptMMADriverAspect();
        getOptRootRecorderBus().register(NloptMMADriverAspect::setRecorder, result);

        return result;
    }

    @Bean
    public NloptObjectAspect optObjectMongoDBAspect() {
        NloptObjectAspect result = new NloptObjectAspect();
        result.setRecorder(objectRecorder());
        return result;
    }

    @Bean
    public OptIndexialRecorder objectRecorder() {
        OptIndexialRecorder result = new OptIndexialRecorder();
        getDBBus().register((obj, db) -> {
            obj.setDbCollection(db.getCollection("opt.obj"));
        }, result);
        getOptRootRecorderBus().register((obj, recorder) -> {
            obj.setUpperIdSupplier(recorder::getCurrentId);
        }, result);
        return result;
    }

    @Bean
    NloptInequalConstraintsAspect InequalConstraintsMongoDBAspect() {
        NloptInequalConstraintsAspect result = new NloptInequalConstraintsAspect();
        result.setRecorder(inequalConstraintsRecorder());
        return result;
    }

    @Bean
    public OptIndexialRecorder inequalConstraintsRecorder() {
        OptIndexialRecorder result = new OptIndexialRecorder();
        getDBBus().register((obj, db) -> {
            obj.setDbCollection(db.getCollection("opt.ineq"));
        }, result);
        getOptRootRecorderBus().register((obj, recorder) -> {
            obj.setUpperIdSupplier(recorder::getCurrentId);
        }, result);
        return result;
    }
}
