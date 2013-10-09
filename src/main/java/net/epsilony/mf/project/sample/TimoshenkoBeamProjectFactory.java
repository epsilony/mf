/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.util.Arrays;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.util.MFConstants;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tb.Factory;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.tb.analysis.GenericFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoBeamProjectFactory implements Factory<SimpMFMechanicalProject> {

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
    public SimpMFMechanicalProject produce() {
        setupRectangleGeom();
        setupBoundaryConditions();
        rectangleProjectFactory.setConstitutiveLaw(timoBeam.constitutiveLaw());
        return rectangleProjectFactory.produce();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        rectangleProjectFactory.setQuadratureDegree(quadratureDegree);
    }

    public Assembler getAssembler() {
        return rectangleProjectFactory.getAssembler();
    }

    public void setAssembler(Assembler assembler) {
        rectangleProjectFactory.setAssembler(assembler);
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
            public boolean isDirichlet() {
                return false;
            }

            @Override
            public double[] getLoad() {
                segment.setDiffOrder(0);
                double[] ds = segment.values(parameter, null);
                return func.value(ds, ds);
            }

            @Override
            public boolean[] getLoadValidity() {
                return null;
            }
        });

        rectangleProjectFactory.setEdgeLoad(LEFT, new AbstractSegmentLoad() {
            GenericFunction<double[], double[]> func = timoBeam.new DirichletFunction();
            GenericFunction<double[], boolean[]> valdFunc = timoBeam.new DirichletMarker();
            double[] coord = new double[2];

            @Override
            public boolean isDirichlet() {
                return true;
            }

            @Override
            public void setParameter(double parm) {
                super.setParameter(parm);
            }

            @Override
            public double[] getLoad() {
                segment.setDiffOrder(0);
                segment.values(parameter, coord);
                return func.value(coord, null);
            }

            @Override
            public boolean[] getLoadValidity() {
                return valdFunc.value(coord, null);
            }
        });
    }

    public static void main(String[] args) {
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoBeamProjectFactory timoFactory = new TimoshenkoBeamProjectFactory();
        timoFactory.setTimoBeam(timoBeam);
        timoFactory.setQuadratureDegree(quadDegree);
        timoFactory.setNodesDistance(quadDomainSize);
        timoFactory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(inflRads));

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
