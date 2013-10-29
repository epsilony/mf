/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.util.MFKey;

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
    INTEGRATE_UNITS_NUM(Integer.class);

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
