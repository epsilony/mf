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
package net.epsilony.mf.opt.persist.config;

import java.util.List;
import java.util.stream.Collectors;

import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.opt.config.OptBaseConfig;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.persist.OptIndexialRecorder;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Aspect
public class IntegralUnitsAspect {
    public static final String BOUNDARY_CHAINS_VERTES = "bndChainsVertes";

    private OptIndexialRecorder recorder;

    private TriangleMarchingIntegralUnitsFactory unitsFactory;

    private static final String POINT_CUT_VALUE = "bean(" + OptBaseConfig.TRIANGLE_MARCHING_INTEGRAL_UNITS_FACTORY
            + ") && execution(void generateUnits())";

    @AfterReturning(value = POINT_CUT_VALUE)
    public void afterGenerateUnits() {
        List<MFEdge> contourHeads = unitsFactory.getContourHeads();
        List<List<double[]>> chainsVertes = contourHeads.stream().map(line -> {
            return line.stream().map(MFLine::getStartCoord).collect(Collectors.toList());
        }).collect(Collectors.toList());
        recorder.record(BOUNDARY_CHAINS_VERTES, chainsVertes);
    }

    public void setRecorder(OptIndexialRecorder recorder) {
        this.recorder = recorder;
    }

    public void setUnitsFactory(TriangleMarchingIntegralUnitsFactory unitsFactory) {
        this.unitsFactory = unitsFactory;
    }

}
