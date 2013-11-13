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

package net.epsilony.mf.process.assembler;

import java.util.EnumMap;
import java.util.Map;

import net.epsilony.mf.process.MFProcessType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Configuration
public class Assemblers {
    public static final Logger logger = LoggerFactory.getLogger(Assembler.class);

    @Bean
    public static Map<MFProcessType, Assembler> mechanicalAssemblersGroup() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new MechanicalVolumeAssembler());
        result.put(MFProcessType.NEUMANN, new NeumannAssembler());
        result.put(MFProcessType.DIRICHLET, new LagrangleDirichletAssembler());
        return result;
    }

    @Bean
    public static Map<MFProcessType, Assembler> poissonAssemblersGroup() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new PoissonVolumeAssembler());
        result.put(MFProcessType.NEUMANN, new NeumannAssembler());
        result.put(MFProcessType.DIRICHLET, new LagrangleDirichletAssembler());
        return result;
    }

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:spring_beans/assemblers.xml")) {
            Map<MFProcessType, Assembler> group = (Map<MFProcessType, Assembler>) context
                    .getBean("poissonAssemblersGroup");
            logger.info("poissonAssemblersGroup: {}", group);
            group = (Map<MFProcessType, Assembler>) context.getBean("mechanicalAssemblersGroup");
            logger.info("mechanicalAssemblersGroup: {}", group);
        }

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Assemblers.class);
        Map<MFProcessType, Assembler> group = (Map<MFProcessType, Assembler>) context.getBean("poissonAssemblersGroup");
        logger.info("poissonAssemblersGroup: {}", group);
        group = (Map<MFProcessType, Assembler>) context.getBean("mechanicalAssemblersGroup");
        logger.info("mechanicalAssemblersGroup: {}", group);
    }
}
