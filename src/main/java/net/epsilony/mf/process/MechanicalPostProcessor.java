/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.geomodel.MFBoundary;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPostProcessor extends PostProcessor {

    ConstitutiveLaw constitutiveLaw;

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public double[] engineeringStrain(double[] coord, MFBoundary bnd) {
        if (2 != getNodeValueDimension()) {
            throw new IllegalStateException();
        }
        setDiffOrder(1);
        double[] displacement = value(coord, bnd);
        return new double[]{displacement[2], displacement[5], displacement[3] + displacement[4]};
    }
}
