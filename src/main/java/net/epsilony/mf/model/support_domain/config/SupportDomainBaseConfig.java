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
package net.epsilony.mf.model.support_domain.config;

import java.util.List;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.util.bus.WeakBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class SupportDomainBaseConfig {
    public static final String SUPPORT_DOMAIN_SEARCHER_PROTO            = "supportDomainSearcherProto";
    public static final String INFLUENCED_SUPPORT_DOMAIN_SEARCHER_PROTO = "influencedSupportDomainSearcherProto";
    public static final String SUPPORT_DOMAIN_HUB                       = "supportDomainHub";

    public static final String SUPPORT_DOMAIN_POINTS_BUS                = "supportDomainPointsBus";

    @Bean(name = SUPPORT_DOMAIN_POINTS_BUS)
    public WeakBus<List<? extends GeomPoint>> supportDomainPointsBus() {
        return new WeakBus<>(SUPPORT_DOMAIN_POINTS_BUS);
    }

    public static final String SUPPORT_DOMAIN_BOUNDARIES_BUS = "supportDomainBoundariesBus";

    @Bean(name = SUPPORT_DOMAIN_BOUNDARIES_BUS)
    public WeakBus<List<? extends MFGeomUnit>> supportDomainBoundariesBus() {
        return new WeakBus<>(SUPPORT_DOMAIN_BOUNDARIES_BUS);
    }

    public static final String SUPPORT_DOMAIN_NODES_BUS = "supportDomainNodesBus";

    @Bean(name = SUPPORT_DOMAIN_NODES_BUS)
    public WeakBus<List<? extends MFNode>> supportDomainNodesBus() {
        return new WeakBus<>(SUPPORT_DOMAIN_NODES_BUS);
    }

}
