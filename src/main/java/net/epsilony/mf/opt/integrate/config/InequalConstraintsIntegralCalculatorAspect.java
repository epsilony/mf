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
package net.epsilony.mf.opt.integrate.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.opt.integrate.InequalConstraintsIntegralCalculator;
import net.epsilony.mf.opt.persist.OptIndexialRecorder;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Aspect
public class InequalConstraintsIntegralCalculatorAspect {

    private static final String INEQUAL_CONSTRAINTS_CALCULATE_PREPARE_POINT_CUT = "bean("
                                                                                        + OptIntegralConfig.INEQUAL_CONSTRAINTS_INTEGRAL_CALCULATOR
                                                                                        + ") && execution(void calculatePrepare())";

    private OptIndexialRecorder recorder;

    @After(value = INEQUAL_CONSTRAINTS_CALCULATE_PREPARE_POINT_CUT)
    public void afterCalculatePreparePointCut(JoinPoint joinPoint) {
        InequalConstraintsIntegralCalculator target = (InequalConstraintsIntegralCalculator) joinPoint.getTarget();

        Map<String, Object> data = new HashMap<>();
        data.put("rangePoints", fectchWeightsCoords(target.getRangeIntegralPoints()));
        data.put("domainVolumePoints", fectchWeightsCoords(target.getDomainVolumeIntegralPoints()));
        data.put("domainBoundaryPoints", fectchWeightsCoords(target.getDomainBoundaryIntegralPoints()));

        recorder.record(data);
    }

    public Map<String, Object> fectchWeightsCoords(List<GeomQuadraturePoint> src) {
        List<Double> weights = new ArrayList<>(src.size());
        List<double[]> coords = new ArrayList<>(src.size());
        for (GeomQuadraturePoint gqp : src) {
            weights.add(gqp.getWeight());
            coords.add(gqp.getGeomPoint().getCoord());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("weights", weights);
        result.put("coords", coords);
        return result;
    }

    public OptIndexialRecorder getRecorder() {
        return recorder;
    }

    public void setRecorder(OptIndexialRecorder recorder) {
        this.recorder = recorder;
    }

}
