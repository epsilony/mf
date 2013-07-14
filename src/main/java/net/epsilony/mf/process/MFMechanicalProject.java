/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.cons_law.ConstitutiveLaw;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMechanicalProject extends MFProject {

    void setConstitutiveLaw(ConstitutiveLaw cLaw);

    ConstitutiveLaw getConstitutiveLaw();
    
}
