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
package net.epsilony.mf.process.integrate.core.twod;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.UnitTypeDrivenIntegratorCore;
import net.epsilony.mf.process.integrate.core.oned.LineIntegratorCore;
import net.epsilony.mf.process.integrate.core.oned.SubLineIntegratorCore;
import net.epsilony.mf.process.integrate.unit.GeomUnitSubdomain;
import net.epsilony.mf.process.integrate.unit.SubLineDomain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class TwoDCoreConf {
    @Bean
    @Scope("prototype")
    public MFIntegratorCore volumeIntegratorCore() {
        return new QuadranglePolygonVolumeIntegratorCore();
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore neumannIntegratorCore() {
        UnitTypeDrivenIntegratorCore result = new UnitTypeDrivenIntegratorCore(MFProcessType.NEUMANN);
        result.register(GeomUnitSubdomain.class, new LineIntegratorCore(MFProcessType.NEUMANN));
        result.register(SubLineDomain.class, new SubLineIntegratorCore(MFProcessType.NEUMANN));
        return result;
    }

    @Bean
    @Scope("prototype")
    public MFIntegratorCore dirichletIntegratorCore() {
        UnitTypeDrivenIntegratorCore result = new UnitTypeDrivenIntegratorCore(MFProcessType.DIRICHLET);
        result.register(GeomUnitSubdomain.class, new LineIntegratorCore(MFProcessType.DIRICHLET));
        result.register(SubLineDomain.class, new SubLineIntegratorCore(MFProcessType.DIRICHLET));
        return result;
    }
}
