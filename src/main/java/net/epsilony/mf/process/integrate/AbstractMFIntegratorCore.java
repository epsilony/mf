/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFIntegratorCore implements MFIntegratorCore {

    protected Assembler assembler;
    protected MFMixer mixer;
    protected MFIntegratePoint integrateUnit;

    @Override
    public Assembler getAssembler() {
        return assembler;
    }

    @Override
    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
        assembler.prepare();
    }

    @Override
    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }

    @Override
    public void setIntegrateUnit(MFIntegratePoint integrateUnit) {
        this.integrateUnit = integrateUnit;
    }
}
