/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.FacetModel;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.RectangleModelFactory;
import net.epsilony.mf.model.RectanglePhM;
import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.assembler.MechanicalLagrangeAssembler;
import net.epsilony.mf.process.integrate.TwoDIntegrateTaskFactory;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.Factory;

/**
 *
 * @author epsilon
 */
public abstract class AbstractRectangleProject implements Factory<SimpMFMechanicalProject> {

    public static final double DEFAULT_DOWN = 0;
    public static final double DEFAULT_LEFT = 0;
    protected static final int DEFAULT_QUADRATURE_DEGREE = 2;
    public static final double DEFAULT_RIGHT = 3;
    public static final double DEFAULT_UP = 3;

    
    TwoDIntegrateTaskFactory integrateTaskFactory = new TwoDIntegrateTaskFactory();
    int quadratureDegree = DEFAULT_QUADRATURE_DEGREE;
    RectanglePhM rect = new RectanglePhM();
    RectangleModelFactory rectangleModelFactory = new RectangleModelFactory();
    double segLenSup = Math.min(DEFAULT_RIGHT - DEFAULT_LEFT, DEFAULT_UP - DEFAULT_DOWN) * 0.3;
    double spaceNodesDistance = segLenSup;

    protected abstract void applyLoadsOnRectangle();

    abstract protected ConstitutiveLaw genConstitutiveLaw();

    public double getEdgePosition(MFRectangleEdge edge) {
        return rect.getEdgePosition(edge);
    }

    public double getHeight() {
        return rect.getHeight();
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public double getWidth() {
        return rect.getWidth();
    }

    public boolean isAvialable() {
        return rect.isAvialable();
    }

    protected void prepare() {
        rect.setEdgePosition(MFRectangleEdge.DOWN, DEFAULT_DOWN);
        rect.setEdgePosition(MFRectangleEdge.RIGHT, DEFAULT_RIGHT);
        rect.setEdgePosition(MFRectangleEdge.UP, DEFAULT_UP);
        rect.setEdgePosition(MFRectangleEdge.LEFT, DEFAULT_LEFT);
        applyLoadsOnRectangle();
        rectangleModelFactory.setRectangleModel(rect);
        rectangleModelFactory.setFractionSizeCap(segLenSup);
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

    public void setEdgePosition(MFRectangleEdge edge, double position) {
        rect.setEdgePosition(edge, position);
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }
}
