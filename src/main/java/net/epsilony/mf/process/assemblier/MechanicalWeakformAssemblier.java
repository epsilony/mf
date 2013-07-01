/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assemblier;

import net.epsilony.mf.cons_law.ConstitutiveLaw;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MechanicalWeakformAssemblier<T extends MechanicalWeakformAssemblier<T>> extends WeakformAssemblier<T> {

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);
}
