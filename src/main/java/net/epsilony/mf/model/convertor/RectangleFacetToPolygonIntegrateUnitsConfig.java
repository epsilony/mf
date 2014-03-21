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
package net.epsilony.mf.model.convertor;

import java.util.ArrayList;

import net.epsilony.mf.integrate.convertor.RectangleFacetToQuadranglePolygonUnits;
import net.epsilony.mf.integrate.convertor.RectangleToQuadranglePolygonIntegrateUnitGrid;
import net.epsilony.mf.integrate.integrator.CascadeIntegrator;
import net.epsilony.mf.integrate.integrator.ConvertorIntegrator;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.RectangleFacet;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.util.HolderProxy;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class RectangleFacetToPolygonIntegrateUnitsConfig {
    @Bean
    public CascadeIntegrator<RectangleFacet, Iterable<? extends PolygonIntegrateUnit>> rectangleFacetIntegrateToPolygonIntegrateUnits() {
        return new ConvertorIntegrator<>(new RectangleFacetToQuadranglePolygonUnits(rectangleToPolygonIntegrateUnits()));
    }

    @Bean
    public Function<MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits() {
        return RectangleToQuadranglePolygonIntegrateUnitGrid
                .expendedInstance(rectangleToQuadranglePolygonIntegrateUnitVertesGrid());
    }

    @Bean
    public Function<MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>> rectangleToQuadranglePolygonIntegrateUnitVertesGrid() {
        return rectangleToQuadranglePolygonIntegrateUnitVertesGridHolderProxy().getProxied();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    HolderProxy<Function<MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>>> rectangleToQuadranglePolygonIntegrateUnitVertesGridHolderProxy() {
        return new HolderProxy(Function.class);
    }

}
