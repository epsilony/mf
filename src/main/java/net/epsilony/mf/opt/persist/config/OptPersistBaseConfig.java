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

import net.epsilony.mf.opt.persist.OptRootRecorder;

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
public class OptPersistBaseConfig {
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
        result.setOptsDBCollection(optDBCollection());
        return result;
    }

    @Bean
    public DBCollection optDBCollection() {
        DBCollection result = mongoDB().getCollection("opt");
        return result;
    }

}
