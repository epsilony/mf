/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorCore;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.util.MFKey;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFIntegratorObserverKey implements MFKey {

    PROCESS_TYPE(MFProcessType.class),
    ASSEMBLER(Assembler.class),
    INTEGRATOR(MFIntegrator.class),
    STATUS(MFIntegratorStatus.class),
    INTEGRATE_UNIT(MFIntegratePoint.class),
    INTEGRATE_UNITS_NUM(Integer.class),
    MIX_RESULT(MixResult.class),
    CORE(MFIntegratorCore.class),
    COORD(double[].class),
    BOUNDARY(GeomUnit.class),
    OUT_NORMAL(double[].class),
    LOAD(double[].class),
    LOAD_VALIDITY(boolean[].class),
    WEIGHT(Double.class),
    LAGRANGLE_SHAPE_FUNCTION(double[].class),
    LAGRANGLE_INDES(TIntArrayList.class);

    private MFIntegratorObserverKey(Class<?> valueType) {
        this.valueType = valueType;
    }
    private final Class<?> valueType;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }

}
