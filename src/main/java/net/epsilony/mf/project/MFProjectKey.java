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

package net.epsilony.mf.project;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.util.MFKey;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFProjectKey implements MFKey {

    VALUE_DIMENSION(Integer.class),
    SPATIAL_DIMENSION(Integer.class),
    ASSEMBLERS_GROUP(Map.class),
    INTEGRATE_UNITS_GROUP(Map.class),
    ANALYSIS_MODEL(AnalysisModel.class),
    SHAPE_FUNCTION(MFShapeFunction.class),
    INFLUENCE_RADIUS_CALCULATOR(InfluenceRadiusCalculator.class),
    CONSTITUTIVE_LAW(ConstitutiveLaw.class);

    private final Class<?> valueType;

    private MFProjectKey(Class<?> valueType) {
        this.valueType = valueType;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }

    public static Map<MFKey, Object> getDefaultSettings() {
        Map<MFKey, Object> result = new HashMap<>();
        result.put(SHAPE_FUNCTION, new MLS());
        return result;
    }
}
