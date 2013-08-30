/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorCore<V, N, D> {

    void integrateVolume(V volumeObj);

    void integrateNeumann(N neumannObj);

    void integrateDirichlet(D dirichletObj);

    void setAssembler(Assembler assembler);

    Assembler getAssembler();

    void setMixer(MFMixer mixer);
}
