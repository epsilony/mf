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
package net.epsilony.mf.process.integrate.core.oned;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class OneDCoreConf {
    @Bean
    @Scope("prototype")
    public MFIntegratorCore volumeIntegratorCore() {
        return new LineIntegratorCore(MFProcessType.VOLUME);
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore neumannIntegratorCore() {
        return new NodeBoundaryIntegratorCore(MFProcessType.NEUMANN);
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore dirichletIntegratorCore() {
        return new NodeBoundaryIntegratorCore(MFProcessType.DIRICHLET);
    }
}
