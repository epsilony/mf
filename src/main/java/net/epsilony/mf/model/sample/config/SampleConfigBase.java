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
package net.epsilony.mf.model.sample.config;

import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.util.NormalGridToPolygonUnitGrid;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.function.ChainFractionizer;
import net.epsilony.mf.model.function.FacetFractionizer;
import net.epsilony.mf.model.function.FacetFractionizer.FacetFractionResult;
import net.epsilony.mf.model.function.SingleLineFractionizer;
import net.epsilony.mf.model.sample.PatchModelFactory2D;
import net.epsilony.mf.util.function.GridInnerPicker;
import net.epsilony.mf.util.function.RectangleToGridCoords;
import net.epsilony.mf.util.math.PartialValueTuple;
import net.epsilony.tb.solid.Facet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Configuration
public abstract class SampleConfigBase {

    private final int defaultGridRowColNum = 8;

    @Bean
    public Function<MFRectangle, List<? extends PolygonIntegrateUnit>> volumeUnitsGenerator() {
        return rectangleToGrids().andThen(new NormalGridToPolygonUnitGrid()).andThen(Iterables::concat)
                .andThen(Lists::newArrayList);
    }

    @Bean
    public Function<MFRectangle, List<double[]>> spaceNodesCoordsGenerator() {
        RectangleToGridCoords.ByNumRowsCols rectToGrids = rectangleToGrids();

        return rectToGrids.andThen(new GridInnerPicker<>()).andThen(Iterables::concat).andThen(Lists::newArrayList);

    }

    @Bean
    public RectangleToGridCoords.ByNumRowsCols rectangleToGrids() {
        RectangleToGridCoords.ByNumRowsCols rectToGrids = new RectangleToGridCoords.ByNumRowsCols();
        rectToGrids.setNumCols(defaultGridRowColNum);
        rectToGrids.setNumRows(defaultGridRowColNum);
        return rectToGrids;
    }

    @Bean
    public Function<Facet, Facet> facetFractionizer() {

        ChainFractionizer chainFractionizer = new ChainFractionizer();
        chainFractionizer.setNodeFactory(MFNode::new);
        chainFractionizer.setSingleLineFractionier(singleLineFractionier());
        FacetFractionizer facetFractionizer = new FacetFractionizer();
        facetFractionizer.setChainFractionier(chainFractionizer);
        return facetFractionizer.andThen(FacetFractionResult::getFacet);
    }

    @Bean
    public SingleLineFractionizer.ByNumberOfNewCoords singleLineFractionier() {
        SingleLineFractionizer.ByNumberOfNewCoords result = new SingleLineFractionizer.ByNumberOfNewCoords(
                defaultGridRowColNum - 2);
        return result;
    }

    @Bean
    public MFRectangle rectangle() {
        MFRectangle result = new MFRectangle();
        result.setDrul(new double[] { -1, 1, 1, -1 });
        return result;
    }

    @Bean
    public abstract Function<double[], PartialValueTuple> field();

    public abstract PatchModelFactory2D patchModelFactory2D();

}