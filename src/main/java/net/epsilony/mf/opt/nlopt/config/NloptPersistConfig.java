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

import javax.annotation.Resource;

import net.epsilony.mf.opt.persist.OptIndexialRecorder;
import net.epsilony.mf.opt.persist.OptRootRecorder;

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
public class NloptPersistConfig {

    @Resource
    DB mongoDB;

    @Resource
    OptRootRecorder optRecorder;

    @Bean
    public NloptMMADriverAspect nloptMMADriverAspect() {
        NloptMMADriverAspect result = new NloptMMADriverAspect();
        result.setRecorder(optRecorder);
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
        result.setDbCollection(objectDBCollection());
        result.setUpperIdSupplier(optRecorder::getCurrentId);
        return result;
    }

    public static final String OBJECT_DB_COLLECTION = "objectDBCollection";

    @Bean(name = OBJECT_DB_COLLECTION)
    public DBCollection objectDBCollection() {
        DBCollection result = mongoDB.getCollection("opt.obj");
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
        result.setDbCollection(inequalConstraintsDBCollection());
        result.setUpperIdSupplier(optRecorder::getCurrentId);
        return result;
    }

    public static final String INEQUAL_CONSTRAINTS_DB_COLLECTION = "inequalConstraintsDBCollection";

    @Bean(name = INEQUAL_CONSTRAINTS_DB_COLLECTION)
    public DBCollection inequalConstraintsDBCollection() {
        return mongoDB.getCollection("opt.ineq");
    }
}
