/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.project.RectangleProjectFactory;
import java.util.Arrays;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tb.Factory;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.process.MFPreprocessorKey;
import net.epsilony.mf.process.assembler.Assemblers;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
import net.epsilony.mf.project.MFProject;
import net.epsilony.tb.analysis.GenericFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoBeamProjectFactory implements Factory<MFProject> {

    TimoshenkoAnalyticalBeam2D timoBeam;
    double influenceRad;
    RectangleProjectFactory rectangleProjectFactory = new RectangleProjectFactory();

    public void setTimoBeam(TimoshenkoAnalyticalBeam2D timoBeam) {
        this.timoBeam = timoBeam;
    }

    public TimoshenkoAnalyticalBeam2D getTimoBeam() {
        return timoBeam;
    }

    public int getQuadratureDegree() {
        return rectangleProjectFactory.getQuadratureDegree();
    }

    @Override
    public MFProject produce() {
        setupRectangleGeom();
        setupBoundaryConditions();
        rectangleProjectFactory.setValueDimension(2);
        rectangleProjectFactory.setConstitutiveLaw(timoBeam.constitutiveLaw());
        rectangleProjectFactory.setAssemblersGroup(Assemblers.mechanicalLagrangle());
        return rectangleProjectFactory.produce();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        rectangleProjectFactory.setQuadratureDegree(quadratureDegree);
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return rectangleProjectFactory.getInfluenceRadiusCalculator();
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        rectangleProjectFactory.setInfluenceRadiusCalculator(influenceRadiusCalculator);
    }

    public double getNodesDistance() {
        return rectangleProjectFactory.getNodesDistance();
    }

    public void setNodesDistance(double nodesDistance) {
        rectangleProjectFactory.setNodesDistance(nodesDistance);
    }

    private void setupRectangleGeom() {
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;

        rectangleProjectFactory.setEdgePosition(DOWN, down);
        rectangleProjectFactory.setEdgePosition(UP, up);
        rectangleProjectFactory.setEdgePosition(LEFT, left);
        rectangleProjectFactory.setEdgePosition(RIGHT, right);
    }

    private void setupBoundaryConditions() {
        rectangleProjectFactory.setEdgeLoad(RIGHT, new AbstractSegmentLoad() {
            final GenericFunction<double[], double[]> func = timoBeam.new NeumannFunction();

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                double[] ds = segment.values(parameter, null);
                return func.value(ds, ds);
            }
        });

        rectangleProjectFactory.setEdgeLoad(LEFT, new AbstractSegmentLoad() {
            GenericFunction<double[], double[]> func = timoBeam.new DirichletFunction();
            GenericFunction<double[], boolean[]> valdFunc = timoBeam.new DirichletMarker();
            double[] coord = new double[2];

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                segment.values(parameter, coord);
                return func.value(coord, null);
            }

            @Override
            public boolean[] getValidity() {
                return valdFunc.value(coord, null);
            }

            @Override
            public boolean isDirichlet() {
                return true;
            }
        });
    }

    public static void main(String[] args) {
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        double quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoBeamProjectFactory timoFactory = new TimoshenkoBeamProjectFactory();
        timoFactory.setTimoBeam(timoBeam);
        timoFactory.setQuadratureDegree(quadDegree);
        timoFactory.setNodesDistance(quadDomainSize);
        timoFactory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(inflRads));

        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(timoFactory.produce());
        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setThreadNum(1);
        processor.getSettings().put(MFPreprocessorKey.INTEGRATOR, factory.produce());
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

    public double getEdgePosition(MFRectangleEdge edge) {
        return rectangleProjectFactory.getEdgePosition(edge);
    }
}
