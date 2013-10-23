/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.project.sample.TimoshenkoBeamProjectFactory;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.process.assembler.Assembler;

import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFMechanicalProject extends SimpMfProject implements MFMechanicalProject {

    ConstitutiveLaw constitutiveLaw;

    public void setConstitutiveLaw(ConstitutiveLaw cLaw) {
        constitutiveLaw = cLaw;
    }

    @Override
    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public static TimoshenkoBeamProjectFactory genTimoshenkoProjectFactory() {

        TimoshenkoAnalyticalBeam2D timoBeam =
                new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 1;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoBeamProjectFactory timoFactory = new TimoshenkoBeamProjectFactory();
        timoFactory.setTimoBeam(timoBeam);
        timoFactory.setQuadratureDegree(quadDegree);
        timoFactory.setNodesDistance(quadDomainSize);
        timoFactory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(inflRads));
        return timoFactory;
    }
}
