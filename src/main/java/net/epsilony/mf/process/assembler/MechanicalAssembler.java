/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.cons_law.ConstitutiveLaw;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MechanicalAssembler extends Assembler {

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);

    ConstitutiveLaw getConstitutiveLaw();
}
