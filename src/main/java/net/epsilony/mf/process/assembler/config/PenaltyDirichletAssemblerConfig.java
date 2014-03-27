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
package net.epsilony.mf.process.assembler.config;

import net.epsilony.mf.process.assembler.PenaltyDirichletAssembler;
import net.epsilony.mf.util.bus.ConsumerBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>xs
 * 
 */
@Configuration
public class PenaltyDirichletAssemblerConfig {
    public static final String DIRICHLET_PENALTY_BUS = "dirichletPenaltyBus";

    @Bean(name = DIRICHLET_PENALTY_BUS)
    public ConsumerBus<Double> dirichletPenaltyBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = AssemblerBaseConfig.DIRICHLET_ASSEMBLER_PROTO)
    public PenaltyDirichletAssembler dirichletAssemblerProto() {
        PenaltyDirichletAssembler result = new PenaltyDirichletAssembler();
        dirichletPenaltyBus().register(result::setPenalty);
        return result;
    }
}
