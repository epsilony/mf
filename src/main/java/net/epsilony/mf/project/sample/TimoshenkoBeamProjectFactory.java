/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.util.Arrays;
import net.epsilony.mf.process.integrate.RectangleTask;
import net.epsilony.mf.process.assembler.MechanicalLagrangeAssembler;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.geomodel.Rectangle2DModel;
import net.epsilony.mf.geomodel.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.project.MFMechanicalProject;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.util.MFConstants;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tb.Factory;
import static net.epsilony.mf.geomodel.MFRectangleEdge.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoBeamProjectFactory implements Factory<MFMechanicalProject> {

    TimoshenkoAnalyticalBeam2D timoBeam;
    RectangleTask rectangleTask;
    Rectangle2DModel rectangleModel;
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

    public TimoshenkoAnalyticalBeam2D getTimoBeam() {
        return timoBeam;
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
    public MFMechanicalProject produce() {
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;
        rectangleTask = new RectangleTask();
        rectangleModel = new Rectangle2DModel();
        rectangleTask.setRectangleModel(rectangleModel);
        rectangleModel.setUp(up);
        rectangleModel.setDown(down);
        rectangleModel.setLeft(left);
        rectangleModel.setRight(right);
        rectangleModel.setNodesDistanceUpperBound(spaceNodesGap);

        rectangleTask.setQuadratureDegree(quadrangleDegree);
        rectangleTask.setVolumeSpecification(null, quadrangleDomainSize);
        rectangleTask.addBoundaryConditionOnEdge(RIGHT, timoBeam.new NeumannFunction(), null);
        rectangleTask.addBoundaryConditionOnEdge(LEFT, timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());

//        rectangleTask.prepareModelAndTask();

        MFShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        MechanicalLagrangeAssembler assembler = new MechanicalLagrangeAssembler();
        assembler.setConstitutiveLaw(constitutiveLaw);
        InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(influenceRad);

        SimpMFMechanicalProject result = new SimpMFMechanicalProject();
        result.setMFIntegrateTask(rectangleTask);
        result.setShapeFunction(shapeFunc);
        result.setAssembler(assembler);
        result.setConstitutiveLaw(constitutiveLaw);
        result.setInfluenceRadiusCalculator(influenceRadsCalc);
        result.setModel(rectangleModel);
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

    public static void main(String[] args) {
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoBeamProjectFactory timoFactory = new TimoshenkoBeamProjectFactory();
        timoFactory.setTimoBeam(timoBeam);
        timoFactory.setQuadrangleDegree(quadDegree);
        timoFactory.setQuadrangleDomainSize(quadDomainSize);
        timoFactory.setSegmentLengthUpperBound(quadDomainSize);
        timoFactory.setInfluenceRad(inflRads);
        timoFactory.setSpaceNodesGap(quadDomainSize);

        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(timoFactory.produce());
        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        processor.preprocess();
        processor.solve();

        PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        double[] engineeringStrain = mpp.engineeringStrain(new double[]{1, 0}, null);
        System.out.println("engineeringStrain = " + Arrays.toString(engineeringStrain));
        double[] expStrain = timoFactory.getTimoBeam().strain(1, 0, null);
        System.out.println("expStraint = " + Arrays.toString(expStrain));
        double[] value = pp.value(new double[]{1, 0}, null);
        System.out.println("value = " + Arrays.toString(value));
    }
}
