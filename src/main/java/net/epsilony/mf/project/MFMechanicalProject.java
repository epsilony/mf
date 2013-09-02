/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.process.assembler.MechanicalAssembler;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMechanicalProject extends MFProject {

    ConstitutiveLaw getConstitutiveLaw();

    @Override
    MechanicalAssembler getAssembler();
}
