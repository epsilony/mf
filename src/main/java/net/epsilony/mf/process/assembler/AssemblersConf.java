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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.process.MFProcessType;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class AssemblersConf {

    @Bean
    public NeumannAssembler neumannAssembler() {
        return new NeumannAssembler();
    }

    @Bean
    public Assembler lagrangleDirichletAssembler() {
        return new LagrangleDirichletAssembler();
    }

    @Bean
    public Assembler poissonVolumeAssembler() {
        return new PoissonVolumeAssembler();
    }

    @Bean
    public Assembler mechanicalVolumeAssembler() {
        return new MechanicalVolumeAssembler();
    }

    @Lazy(true)
    @Bean(name = "dirichletPenalty")
    public double defaultDirichletPenalty() {
        return 1e6;
    }

    @Bean
    @Lazy(true)
    public Assembler penaltyDirichletAssembler() {
        return new PenaltyDirichletAssembler(defaultDirichletPenalty());
    }

    @Bean
    public Map<MFProcessType, Assembler> mechanicalAssemblersGroup() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, mechanicalVolumeAssembler());
        result.put(MFProcessType.NEUMANN, neumannAssembler());
        result.put(MFProcessType.DIRICHLET, lagrangleDirichletAssembler());
        return result;
    }

    @Bean
    public Map<MFProcessType, Assembler> poissonAssemblersGroup() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, poissonVolumeAssembler());
        result.put(MFProcessType.NEUMANN, neumannAssembler());
        result.put(MFProcessType.DIRICHLET, lagrangleDirichletAssembler());
        return result;
    }

    @Bean
    @Lazy(true)
    public Map<MFProcessType, Assembler> mechanicalPenaltyAssemblersGroup() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, mechanicalVolumeAssembler());
        result.put(MFProcessType.NEUMANN, neumannAssembler());
        result.put(MFProcessType.DIRICHLET, penaltyDirichletAssembler());
        return result;
    }

    @Bean
    @Lazy(true)
    Map<MFProcessType, Assembler> poissonPenaltyAssemblersGroup() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, poissonVolumeAssembler());
        result.put(MFProcessType.NEUMANN, neumannAssembler());
        result.put(MFProcessType.DIRICHLET, penaltyDirichletAssembler());
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<Map<MFProcessType, Assembler>> groups = new LinkedList<>();
        List<String> descriptions = new LinkedList<>();

        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:spring_beans/assemblers.xml")) {
            String descriptionPrefix = "xml:";
            String[] beanNames = new String[] { "poissonAssemblersGroup", "mechanicalAssemblersGroup" };
            for (String beanName : beanNames) {
                groups.add((Map<MFProcessType, Assembler>) context.getBean(beanName));
                descriptions.add(descriptionPrefix + beanName);
            }
        }

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AssemblersConf.class)) {
            String descriptionPrefix = "javaConfig: ";
            String[] beanNames = new String[] { "poissonAssemblersGroup", "mechanicalAssemblersGroup",
                    "poissonPenaltyAssemblersGroup", "mechanicalPenaltyAssemblersGroup" };
            for (String beanName : beanNames) {
                groups.add((Map<MFProcessType, Assembler>) context.getBean(beanName));
                descriptions.add(descriptionPrefix + beanName);
            }
        }

        Iterator<String> desIter = descriptions.iterator();
        for (Map<MFProcessType, Assembler> group : groups) {
            System.out.println(desIter.next() + group);
        }
    }
}
