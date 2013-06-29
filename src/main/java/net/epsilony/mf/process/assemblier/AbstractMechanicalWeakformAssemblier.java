/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assemblier;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMechanicalWeakformAssemblier extends AbstractWeakformAssemblier implements MechanicalWeakformAssemblier{

    protected ConstitutiveLaw constitutiveLaw;
    protected DenseMatrix constitutiveLawMatrixCopy;

    public AbstractMechanicalWeakformAssemblier() {
    }

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
        constitutiveLawMatrixCopy = new DenseMatrix(constitutiveLaw.getMatrix());
    }

    @Override
    public boolean isUpperSymmertric() {
        return constitutiveLaw.isSymmetric();
    }
}
