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
package net.epsilony.mf.model.search.config;

import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.search.SimpChordCenterRangeSearcher;
import net.epsilony.mf.util.bus.WeakBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class TwoDSimpBoundariesRangeSearcherConfig {

    @Resource(name = SearcherBaseConfig.SEARCHER_BOUNDARIES_BUS)
    WeakBus<List<? extends MFLine>> boundariesBus;

    @Bean(name = SearcherBaseConfig.BOUNDARIES_RANGE_SEARCHER_PROTO)
    @Scope("prototype")
    public SimpChordCenterRangeSearcher<MFLine> boundariesRangeSearcherProto() {
        SimpChordCenterRangeSearcher<MFLine> result = new SimpChordCenterRangeSearcher<>();
        boundariesBus.register(SimpChordCenterRangeSearcher::setBoundaries, result);
        return result;
    }
}
