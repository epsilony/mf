/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.io.Serializable;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorCore extends Serializable {

    void integrateVolume(MFIntegratePoint volumeObj);

    void integrateNeumann(MFIntegratePoint neumannObj);

    void integrateDirichlet(MFIntegratePoint dirichletObj);

    void setAssembler(Assembler assembler);

    Assembler getAssembler();

    void setMixer(MFMixer mixer);
}
