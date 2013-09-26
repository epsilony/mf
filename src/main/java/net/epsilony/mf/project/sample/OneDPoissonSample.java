/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.util.Arrays;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.project.OneDPoisson;
import net.epsilony.mf.shape_func.MFShapeFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoissonSample implements MFProject {

    public enum Choice {

        ZERO, CONSTANT, LINEAR, TRIGONOMETRIC;

        UnivariateFunction getVolumeLoad() {
            return volumeLoads[ordinal()];
        }

        double[] getStartEndDirichlet() {
            return Arrays.copyOf(startEndDirichlets[ordinal()], 2);
        }

        UnivariateFunction getSolution() {
            return solutions[ordinal()];
        }
        static final UnivariateFunction[] volumeLoads = new UnivariateFunction[]{
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return 0;
                }
            },
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return 8;
                }
            },
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return 8 + 16 * x;
                }
            },
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x);
                }
            }
        };
        static final double[][] startEndDirichlets = new double[][]{
            {0, 1},
            {0, 1},
            {0, 1},
            {0, 0}
        };
        static final UnivariateFunction[] solutions = new UnivariateFunction[]{
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return x;
                }
            },
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return -4 * x * x + 5 * x;
                }
            },
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return -8.0 / 3 * x * x * x - 4 * x * x + 23.0 / 3 * x;
                }
            },
            new UnivariateFunction() {
                @Override
                public double value(double x) {
                    return Math.sin(Math.PI * 2 * x);
                }
            }
        };
    }
    public static final int DEFAULT_QUADRATURE_DEGREE = 2;
    public static final double DEFAULT_NODES_DISTANCES_UPPER_BOUND = 0.1;
    public static final double DEFAULT_QUADRAUTRE_DOMAIN_SIZE_UPPER_BOUND = 0.1;
    public static final double DEFAULT_INFLUENCE_RADIUS_RATIO = 2;

    public OneDPoissonSample(Choice choice) {
        this.choice = choice;
        oneDPoisson.setStart(0);
        oneDPoisson.setEnd(1);
        oneDPoisson.setBoudaryLoadAtEnd(new double[]{choice.getStartEndDirichlet()[1]}, new boolean[]{true});
        oneDPoisson.setBoudaryLoadAtStart(new double[]{choice.getStartEndDirichlet()[0]}, new boolean[]{true});
        oneDPoisson.setVolumeLoadFunction(choice.getVolumeLoad());
        oneDPoisson.setQuadratureDegree(DEFAULT_QUADRATURE_DEGREE);
        oneDPoisson.setIntegrateDomainUpperBound(DEFAULT_QUADRAUTRE_DOMAIN_SIZE_UPPER_BOUND);
        oneDPoisson.setNodesDistanceUpperBound(DEFAULT_NODES_DISTANCES_UPPER_BOUND);
        _setInfluenceRadiusRatio(DEFAULT_INFLUENCE_RADIUS_RATIO);
    }
    OneDPoisson oneDPoisson = new OneDPoisson();
    double influenceRadiusRatio;
    Choice choice;

    public void setNodesDistanceUpperBound(double nodesDistanceUpperBound) {
        oneDPoisson.setNodesDistanceUpperBound(nodesDistanceUpperBound);
    }

    public void setQuadratureDegree(int degree) {
        oneDPoisson.setQuadratureDegree(degree);
    }

    public void setIntegrateDomainUpperBound(double integrateDomainUpperBound) {
        oneDPoisson.setIntegrateDomainUpperBound(integrateDomainUpperBound);
    }

    @Override
    public MFSolver getMFSolver() {
        return oneDPoisson.getMFSolver();
    }

    @Override
    public Assembler getAssembler() {
        return oneDPoisson.getAssembler();
    }

    @Override
    public MFIntegrateTask getMFIntegrateTask() {
        return oneDPoisson.getMFIntegrateTask();
    }

    @Override
    public AnalysisModel getModel() {
        return oneDPoisson.getModel();
    }

    @Override
    public MFShapeFunction getShapeFunction() {
        return oneDPoisson.getShapeFunction();
    }

    public double getNodesDistanceUpperBound() {
        return oneDPoisson.getNodesDistanceUpperBound();
    }

    public double getNodesDistance() {
        return oneDPoisson.getNodesDistance();
    }

    public int getIntegrateDomainNum() {
        return oneDPoisson.getIntegrateDomainNum();
    }

    public double getIntegrateDomainLength() {
        return oneDPoisson.getIntegrateDomainLength();
    }

    public double getInfluenceRadius() {
        return getNodesDistance() * influenceRadiusRatio;
    }

    public double getInfluenceRadiusRatio() {
        return influenceRadiusRatio;
    }

    public void setInfluenceRadiusRatio(double influenceRadiusRatio) {
        _setInfluenceRadiusRatio(influenceRadiusRatio);
    }

    private void _setInfluenceRadiusRatio(double influenceRadiusRatio) {
        this.influenceRadiusRatio = influenceRadiusRatio;
    }

    @Override
    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        oneDPoisson.setInfluenceRadius(getInfluenceRadius());
        return oneDPoisson.getInfluenceRadiusCalculator();
    }

    public Choice getChoice() {
        return choice;
    }

    @Override
    public int getDimension() {
        return oneDPoisson.getDimension();
    }

    public static void main(String[] args) {
        OneDPoissonSample sample = new OneDPoissonSample(Choice.LINEAR);
        MFLinearProcessor processor = new MFLinearProcessor();
        processor.setProject(sample);
        processor.preprocess();
    }
}
