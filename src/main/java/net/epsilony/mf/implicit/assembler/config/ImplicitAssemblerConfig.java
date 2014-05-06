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
package net.epsilony.mf.implicit.assembler.config;

import net.epsilony.mf.implicit.assembler.ApproximateVolumeAssembler;
import net.epsilony.mf.process.assembler.config.AssemblerBaseConfig;
import net.epsilony.mf.process.assembler.config.LagrangleDirichletAssemblerConfig;
import net.epsilony.mf.process.assembler.config.NeumannAssemblerConfig;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@Import({ AssemblerBaseConfig.class, LagrangleDirichletAssemblerConfig.class, NeumannAssemblerConfig.class })
public class ImplicitAssemblerConfig extends ApplicationContextAwareImpl {

    @Bean(name = AssemblerBaseConfig.VOLUME_ASSEMBLER_PROTO)
    @Scope("prototype")
    public ApproximateVolumeAssembler volumeAssembler() {
        return new ApproximateVolumeAssembler();
    }
}
