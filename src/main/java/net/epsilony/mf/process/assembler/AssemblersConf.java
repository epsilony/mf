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

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class AssemblersConf {

    @Bean
    @Scope("prototype")
    public VirtualLoadWorkAssembler neumannAssembler() {
        return new VirtualLoadWorkAssembler();
    }

    @Bean
    @Scope("prototype")
    public Assembler lagrangleDirichletAssembler() {
        return new LagrangleDirichletAssembler();
    }

    @Bean
    @Scope("prototype")
    public Assembler poissonVolumeAssembler() {
        return new PoissonVolumeAssembler();
    }

    @Bean
    @Scope("prototype")
    public Assembler mechanicalVolumeAssembler() {
        return new MechanicalVolumeAssembler();
    }

    @Lazy(true)
    @Bean(name = "dirichletPenalty")
    @Scope("prototype")
    public double defaultDirichletPenalty() {
        return 1e6;
    }

    @Bean
    @Lazy(true)
    @Scope("prototype")
    public Assembler penaltyDirichletAssembler() {
        return new PenaltyDirichletAssembler(defaultDirichletPenalty());
    }

    @Bean
    @Scope("prototype")
    public Map<AssemblerType, Assembler> mechanicalAssemblersGroup() {
        EnumMap<AssemblerType, Assembler> result = new EnumMap<>(AssemblerType.class);
        result.put(AssemblerType.ASM_VOLUME, mechanicalVolumeAssembler());
        result.put(AssemblerType.ASM_NEUMANN, neumannAssembler());
        result.put(AssemblerType.ASM_DIRICHLET, lagrangleDirichletAssembler());
        return result;
    }

    @Bean
    @Scope("prototype")
    public Map<AssemblerType, Assembler> poissonAssemblersGroup() {
        EnumMap<AssemblerType, Assembler> result = new EnumMap<>(AssemblerType.class);
        result.put(AssemblerType.ASM_VOLUME, poissonVolumeAssembler());
        result.put(AssemblerType.ASM_NEUMANN, neumannAssembler());
        result.put(AssemblerType.ASM_DIRICHLET, lagrangleDirichletAssembler());
        return result;
    }

    @Bean
    @Lazy(true)
    @Scope("prototype")
    public Map<AssemblerType, Assembler> mechanicalPenaltyAssemblersGroup() {
        EnumMap<AssemblerType, Assembler> result = new EnumMap<>(AssemblerType.class);
        result.put(AssemblerType.ASM_VOLUME, mechanicalVolumeAssembler());
        result.put(AssemblerType.ASM_NEUMANN, neumannAssembler());
        result.put(AssemblerType.ASM_DIRICHLET, penaltyDirichletAssembler());
        return result;
    }

    @Bean
    @Lazy(true)
    @Scope("prototype")
    Map<AssemblerType, Assembler> poissonPenaltyAssemblersGroup() {
        EnumMap<AssemblerType, Assembler> result = new EnumMap<>(AssemblerType.class);
        result.put(AssemblerType.ASM_VOLUME, poissonVolumeAssembler());
        result.put(AssemblerType.ASM_NEUMANN, neumannAssembler());
        result.put(AssemblerType.ASM_DIRICHLET, penaltyDirichletAssembler());
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<Map<AssemblerType, Assembler>> groups = new LinkedList<>();
        List<String> descriptions = new LinkedList<>();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AssemblersConf.class)) {
            String descriptionPrefix = "javaConfig: ";
            String[] beanNames = new String[] { "poissonAssemblersGroup", "mechanicalAssemblersGroup",
                    "poissonPenaltyAssemblersGroup", "mechanicalPenaltyAssemblersGroup" };
            for (String beanName : beanNames) {
                groups.add((Map<AssemblerType, Assembler>) context.getBean(beanName));
                descriptions.add(descriptionPrefix + beanName);
            }
        }

        Iterator<String> desIter = descriptions.iterator();
        for (Map<AssemblerType, Assembler> group : groups) {
            System.out.println(desIter.next() + group);
        }
    }
}
