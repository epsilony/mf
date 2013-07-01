/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.process.assembler.MechanicalLagrangeAssembler;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.model.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tb.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoStandardTask implements Factory<MFProject> {

    TimoshenkoAnalyticalBeam2D timoBeam;
    RectangleTask rectProject;
    double spaceNdsGap;
    double influenceRad;

    public TimoshenkoAnalyticalBeam2D getTimoshenkoAnalytical() {
        return timoBeam;
    }

    public TimoshenkoStandardTask(
            TimoshenkoAnalyticalBeam2D timoBeam,
            double segLengthUpBnd,
            double quadDomainSize,
            int quadDegree) {

        this.timoBeam = timoBeam;
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;
        rectProject = new RectangleTask(left, down, right, up, segLengthUpBnd);
        rectProject.setSegmentQuadratureDegree(quadDegree);
        rectProject.setVolumeSpecification(null, quadDomainSize, quadDegree);
        rectProject.addBoundaryConditionOnEdge("r", timoBeam.new NeumannFunction(), null);
        rectProject.addBoundaryConditionOnEdge("l", timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());
    }

    public ConstitutiveLaw constitutiveLaw() {
        return timoBeam.constitutiveLaw();
    }


    @Override
    public MFProject produce() {
        Model2D model = rectProject.model(spaceNdsGap);
        MFShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        MechanicalLagrangeAssembler assembler = new MechanicalLagrangeAssembler();
        assembler.setConstitutiveLaw(constitutiveLaw);
        InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(influenceRad);

        MFProcessorFactory result = new MFProcessorFactory();
        result.setMFQuadratureTask(rectProject);
        result.setShapeFunction(shapeFunc);
        assembler.setConstitutiveLaw(constitutiveLaw);
        result.setAssembler(assembler);
        model.updateInfluenceAndSupportDomains(influenceRadsCalc);
        result.setModel(model);
        return result;
    }

    public double getSpaceNdsGap() {
        return spaceNdsGap;
    }

    public void setSpaceNdsGap(double spaceNdsGap) {
        this.spaceNdsGap = spaceNdsGap;
    }

    public double getInfluenceRad() {
        return influenceRad;
    }

    public void setInfluenceRad(double influenceRad) {
        this.influenceRad = influenceRad;
    }
}
