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
public class TimoshenkStandardProjectFactory implements Factory<MFProject> {

    TimoshenkoAnalyticalBeam2D timoBeam;
    RectangleTask rectangleTask;
    double spaceNodesGap = Double.POSITIVE_INFINITY;
    double influenceRad;
    private double segmentLengthUpperBound;
    private int quadrangleDegree;
    private double quadrangleDomainSize;

    public TimoshenkoAnalyticalBeam2D getTimoshenkoAnalyticalBeam() {
        return timoBeam;
    }

    public void setTimoBeam(TimoshenkoAnalyticalBeam2D timoBeam) {
        this.timoBeam = timoBeam;
    }

    public double getSegmentLengthUpperBound() {
        return segmentLengthUpperBound;
    }

    public void setSegmentLengthUpperBound(double segmentLengthUpperBound) {
        this.segmentLengthUpperBound = segmentLengthUpperBound;
    }

    public int getQuadrangleDegree() {
        return quadrangleDegree;
    }

    public void setQuadrangleDegree(int quadrangleDegree) {
        this.quadrangleDegree = quadrangleDegree;
    }

    public double getQuadrangleDomainSize() {
        return quadrangleDomainSize;
    }

    public void setQuadrangleDomainSize(double quadrangleDomainSize) {
        this.quadrangleDomainSize = quadrangleDomainSize;
    }

    public ConstitutiveLaw constitutiveLaw() {
        return timoBeam.constitutiveLaw();
    }

    @Override
    public MFProject produce() {
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;
        rectangleTask = new RectangleTask();
        rectangleTask.setUp(up);
        rectangleTask.setDown(down);
        rectangleTask.setLeft(left);
        rectangleTask.setRight(right);
        rectangleTask.setSegmentLengthUpperBound(segmentLengthUpperBound);
        rectangleTask.setSegmentQuadratureDegree(quadrangleDegree);
        rectangleTask.setVolumeSpecification(null, quadrangleDomainSize, quadrangleDegree);
        rectangleTask.addBoundaryConditionOnEdge("r", timoBeam.new NeumannFunction(), null);
        rectangleTask.addBoundaryConditionOnEdge("l", timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());
        rectangleTask.setSpaceNodesDistance(spaceNodesGap);
        rectangleTask.prepareModelAndTask();
        Model2D model = rectangleTask.getModel();
        MFShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        MechanicalLagrangeAssembler assembler = new MechanicalLagrangeAssembler();
        assembler.setConstitutiveLaw(constitutiveLaw);
        InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(influenceRad);

        SimpMfProject result = new SimpMfProject();
        result.setMFQuadratureTask(rectangleTask);
        result.setShapeFunction(shapeFunc);
        assembler.setConstitutiveLaw(constitutiveLaw);
        result.setAssembler(assembler);
        model.updateInfluenceAndSupportDomains(influenceRadsCalc);
        result.setModel(model);
        return result;
    }

    public double getSpaceNodesGap() {
        return spaceNodesGap;
    }

    public void setSpaceNodesGap(double spaceNodesGap) {
        this.spaceNodesGap = spaceNodesGap;
    }

    public double getInfluenceRad() {
        return influenceRad;
    }

    public void setInfluenceRad(double influenceRad) {
        this.influenceRad = influenceRad;
    }
}
