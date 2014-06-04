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
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.sample.PatchModelFactory2D;
import net.epsilony.mf.util.function.GridInnerPicker;
import net.epsilony.mf.util.function.RectangleToGridCoords;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Configuration
public abstract class SampleConfigBase extends ApplicationContextAwareImpl {

    private final int          defaultGridRowColNum    = 8;

    // optional:
    public static final String RECT_SAMPLE_ROW_COL_NUM = "rectSampleRowColNum";

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

        int gridRowColNum = getGridRowColume();
        rectToGrids.setNumCols(gridRowColNum);
        rectToGrids.setNumRows(gridRowColNum);
        return rectToGrids;
    }

    @Bean
    public Function<MFFacet, MFFacet> facetFractionizer() {

        ChainFractionizer chainFractionizer = new ChainFractionizer();
        chainFractionizer.setNodeFactory(MFNode::new);
        chainFractionizer.setLineFactory(SimpMFLine::new);
        chainFractionizer.setSingleLineFractionier(singleLineFractionier());
        FacetFractionizer facetFractionizer = new FacetFractionizer();
        facetFractionizer.setChainFractionier(chainFractionizer);
        return facetFractionizer.andThen(FacetFractionResult::getFacet);
    }

    @Bean
    public SingleLineFractionizer.ByNumberOfNewCoords singleLineFractionier() {
        int gridRowColNum = getGridRowColume();
        SingleLineFractionizer.ByNumberOfNewCoords result = new SingleLineFractionizer.ByNumberOfNewCoords(
                gridRowColNum - 2);
        return result;
    }

    private int getGridRowColume() {
        int gridRowColNum = defaultGridRowColNum;
        if (applicationContext.containsBean(RECT_SAMPLE_ROW_COL_NUM)) {
            gridRowColNum = applicationContext.getBean(RECT_SAMPLE_ROW_COL_NUM, int.class);
        }
        return gridRowColNum;
    }

    @Bean
    public MFRectangle rectangle() {
        MFRectangle result = new MFRectangle();
        result.setDrul(new double[] { -1, 1, 1, -1 });
        return result;
    }

    @Bean
    public abstract Function<double[], PartialTuple> field();

    public abstract PatchModelFactory2D patchModelFactory2D();

}