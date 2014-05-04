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
package net.epsilony.mf.process.config;

import java.util.ArrayList;

import net.epsilony.mf.model.config.CommonAnalysisModelHubConfig;
import net.epsilony.mf.model.config.LagrangleDirichletNodesBusConfig;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.support_domain.config.CenterPerturbSupportDomainSearcherConfig;
import net.epsilony.mf.process.assembler.config.AssemblerBaseConfig;
import net.epsilony.mf.process.assembler.config.LagrangleDirichletAssemblerConfig;
import net.epsilony.mf.process.assembler.config.NeumannAssemblerConfig;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.shape_func.config.MLSConfig;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ProcessConfigs {
    static public ArrayList<Class<?>> commonSimpConfigClasses() {
        return Lists.newArrayList(ModelBusConfig.class, LagrangleDirichletNodesBusConfig.class,
                AssemblerBaseConfig.class, NeumannAssemblerConfig.class, LagrangleDirichletAssemblerConfig.class,
                CenterPerturbSupportDomainSearcherConfig.class, CommonAnalysisModelHubConfig.class, MixerConfig.class,
                MLSConfig.class);
    }

    static public ArrayList<Class<?>> simpConfigClasses(Class<?> volumeAssemblerConfig, Class<?> influenceConfig,
            Class<?> searcherConfig, Class<?> integralConfig) {
        ArrayList<Class<?>> result = commonSimpConfigClasses();
        result.add(volumeAssemblerConfig);
        result.add(influenceConfig);
        result.add(searcherConfig);
        result.add(integralConfig);
        return result;
    }
}
