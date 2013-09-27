/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.tb.Factory;
import static java.lang.Math.*;
import java.util.Arrays;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.model.FacetModel;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.assembler.MechanicalLagrangeAssembler;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.util.MFConstants;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.mf.model.RectangleModelFactory;
import net.epsilony.mf.model.RectanglePhM;
import net.epsilony.mf.model.load.ConstantSegmentLoad;
import net.epsilony.mf.process.integrate.TwoDIntegrateTaskFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TensionBar implements Factory<SimpMFMechanicalProject> {

    public static final double DEFAULT_DOWN = 0, DEFAULT_RIGHT = 3, DEFAULT_UP = 3, DEFAULT_LEFT = 0;
    private static final int DEFAULT_QUADRATURE_DEGREE = 2;
    RectanglePhM rect = new RectanglePhM();
    double segLenSup = min(DEFAULT_RIGHT - DEFAULT_LEFT, DEFAULT_UP - DEFAULT_DOWN) * 0.3;
    double tension = 2000;
    double E = 1000;
    double mu = 0.3;
    double spaceNodesDistance = segLenSup;
    int quadratureDegree = DEFAULT_QUADRATURE_DEGREE;
    RectangleModelFactory rectangleModelFactory = new RectangleModelFactory();
    TwoDIntegrateTaskFactory integrateTaskFactory = new TwoDIntegrateTaskFactory();

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }

    public double getEdgePosition(MFRectangleEdge edge) {
        return rect.getEdgePosition(edge);
    }

    public void setEdgePosition(MFRectangleEdge edge, double position) {
        rect.setEdgePosition(edge, position);
    }

    public boolean isAvialable() {
        return rect.isAvialable();
    }

    public double getWidth() {
        return rect.getWidth();
    }

    public double getHeight() {
        return rect.getHeight();
    }

    @Override
    public SimpMFMechanicalProject produce() {
        prepare();
      
        SimpMFMechanicalProject result = new SimpMFMechanicalProject();
        FacetModel model = rectangleModelFactory.produce();
        integrateTaskFactory.setAnalysisModel(model);
        integrateTaskFactory.setQuadratureDegree(quadratureDegree);
        result.setMFIntegrateTask(integrateTaskFactory.produce());
        result.setShapeFunction(new MLS());
        result.setAssembler(new MechanicalLagrangeAssembler());
        result.setConstitutiveLaw(genConstitutiveLaw());
        InfluenceRadiusCalculator influenceRadsCalc = new EnsureNodesNum(segLenSup * 1.5, 10);
        //InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(segLenSup * 2);
        result.setInfluenceRadiusCalculator(influenceRadsCalc);
        result.setModel(model);
        return result;
    }

    private void prepare() {

        rect.setEdgePosition(DOWN, DEFAULT_DOWN);
        rect.setEdgePosition(RIGHT, DEFAULT_RIGHT);
        rect.setEdgePosition(UP, DEFAULT_UP);
        rect.setEdgePosition(LEFT, DEFAULT_LEFT);

        ConstantSegmentLoad leftLoad = new ConstantSegmentLoad();
        leftLoad.setLoad(new double[]{0, 0});
        leftLoad.setLoadValidity(new boolean[]{true, false});
        rect.setEdgeLoad(LEFT, leftLoad);

        ConstantSegmentLoad rightLoad = new ConstantSegmentLoad();
        rightLoad.setLoad(new double[]{tension, 0});
        rect.setEdgeLoad(RIGHT, rightLoad);

        ConstantSegmentLoad downLoad = new ConstantSegmentLoad();
        downLoad.setLoad(new double[]{0, 0});
        downLoad.setLoadValidity(new boolean[]{false, true});
        rect.setEdgeLoad(DOWN, downLoad);
        rectangleModelFactory.setRectangleModel(rect);
        rectangleModelFactory.setFractionSizeCap(segLenSup);
    }

    private ConstitutiveLaw genConstitutiveLaw() {
        return new PlaneStress(E, mu);
    }

    public static void main(String[] args) {

        TensionBar tensionBar = new TensionBar();
        SimpMFMechanicalProject project = tensionBar.produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        processor.preprocess();
        processor.solve();

        PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        int stepNum = 20;

        for (int i = 0; i < stepNum; i++) {
            double[] pt = new double[]{2.99, 0.01 + 2.98 * (i * 1.0 / (stepNum - 1))};
            double[] strain = mpp.engineeringStrain(pt, null);
            double[] value = mpp.value(pt, null);

            System.out.println("pt = " + Arrays.toString(pt) + ", value = " + Arrays.toString(value) + ", eng strain = " + Arrays.toString(strain));
        }
    }
}
