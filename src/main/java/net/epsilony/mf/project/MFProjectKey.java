/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
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
    INTEGRATE_TASKS(MFIntegrateTask.class),
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
