/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.Map;
import java.util.Random;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.RectangleModelFactory;
import net.epsilony.mf.model.RectanglePhM;
import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.TwoDIntegrateTaskFactory;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.Factory;
import static net.epsilony.mf.project.MFProjectKey.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleProjectFactory implements Factory<MFProject> {

    private static final int TWO_D_SPATIAL_DIMENSION = 2;
    public static final double DEFAULT_DOWN = 0;
    public static final double DEFAULT_LEFT = 0;
    protected static final int DEFAULT_QUADRATURE_DEGREE = 2;
    public static final double DEFAULT_RIGHT = 3;
    public static final double DEFAULT_UP = 3;
    TwoDIntegrateTaskFactory integrateTaskFactory = new TwoDIntegrateTaskFactory();
    int quadratureDegree = DEFAULT_QUADRATURE_DEGREE;
    RectanglePhM rect = new RectanglePhM();
    RectangleModelFactory rectangleModelFactory = new RectangleModelFactory();
    double nodesDistance = Math.min(DEFAULT_RIGHT - DEFAULT_LEFT, DEFAULT_UP - DEFAULT_DOWN) * 0.3;
    ConstitutiveLaw constitutiveLaw;
    MFShapeFunction shapeFunction = new MLS();
    InfluenceRadiusCalculator influenceRadiusCalculator;
    Map<MFProcessType, Assembler> assemblersGroup;
    int valueDimension;

    public RectangleProjectFactory() {
        rect.setEdgePosition(MFRectangleEdge.DOWN, DEFAULT_DOWN);
        rect.setEdgePosition(MFRectangleEdge.RIGHT, DEFAULT_RIGHT);
        rect.setEdgePosition(MFRectangleEdge.UP, DEFAULT_UP);
        rect.setEdgePosition(MFRectangleEdge.LEFT, DEFAULT_LEFT);
    }

    protected InfluenceRadiusCalculator genDefaultInfluenceRadiusCalculator() {
        return new EnsureNodesNum(nodesDistance * 1.5, 10);
    }

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

    @Override
    public MFProject produce() {
        SimpMFProject result = new SimpMFProject();

        rectangleModelFactory.setRectangleModel(rect);
        rectangleModelFactory.setFractionSizeCap(nodesDistance);
        AnalysisModel model = rectangleModelFactory.produce();

        result.put(ANALYSIS_MODEL, model);

        integrateTaskFactory.setAnalysisModel(model);
        integrateTaskFactory.setQuadratureDegree(quadratureDegree);
        result.put(INTEGRATE_UNITS_GROUP, integrateTaskFactory.produce());

        result.put(SHAPE_FUNCTION, shapeFunction);

        result.put(ASSEMBLERS_GROUP, assemblersGroup);

        result.put(CONSTITUTIVE_LAW, constitutiveLaw);

        if (null == influenceRadiusCalculator) {
            influenceRadiusCalculator = genDefaultInfluenceRadiusCalculator();
        }
        result.put(INFLUENCE_RADIUS_CALCULATOR, influenceRadiusCalculator);

        result.put(SPATIAL_DIMENSION, TWO_D_SPATIAL_DIMENSION);

        result.put(VALUE_DIMENSION, valueDimension);

        return result;
    }

    public int getValueDimension() {
        return valueDimension;
    }

    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }

    public void setEdgeLoad(MFRectangleEdge edge, MFLoad load) {
        rect.setEdgeLoad(edge, load);
    }

    public void setEdgePosition(MFRectangleEdge edge, double position) {
        rect.setEdgePosition(edge, position);
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public Map<MFProcessType, Assembler> getAssemblersGroup() {
        return assemblersGroup;
    }

    public void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        this.assemblersGroup = assemblersGroup;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public double getNodesDistance() {
        return nodesDistance;
    }

    public void setNodesDistance(double nodesDistance) {
        this.nodesDistance = nodesDistance;
    }

    public void setSpaceNodesDisturbRatio(double spaceNodesDisturbRatio) {
        rectangleModelFactory.setSpaceNodesDisturbRatio(spaceNodesDisturbRatio);
    }

    public void setDisturbRand(Random disturbRand) {
        rectangleModelFactory.setDisturbRand(disturbRand);
    }

    public double getSpaceNodesDisturbRatio() {
        return rectangleModelFactory.getSpaceNodesDisturbRatio();
    }

    public void setVolumeLoad(MFLoad load) {
        rect.setVolumeLoad(load);
    }
}
