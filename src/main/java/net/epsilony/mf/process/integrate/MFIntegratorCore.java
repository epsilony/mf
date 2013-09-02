/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.io.Serializable;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorCore<V, N, D> extends Serializable {

    void integrateVolume(V volumeObj);

    void integrateNeumann(N neumannObj);

    void integrateDirichlet(D dirichletObj);

    void setAssembler(Assembler assembler);

    Assembler getAssembler();

    void setMixer(MFMixer mixer);
}
