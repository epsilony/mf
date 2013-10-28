/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.project.RectangleProjectFactory;
import java.util.Random;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.project.MFProject;
import net.epsilony.tb.Factory;
import net.epsilony.tb.analysis.ArrvarFunction;
import net.epsilony.tb.analysis.Math2D;
import static org.apache.commons.math3.util.FastMath.*;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.VolumeLoad;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.MFPreprocessorKey;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.assembler.Assemblers;
import net.epsilony.mf.process.assembler.PoissonVolumeAssembler;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TwoDPoissonSampleFactory implements Factory<MFProject> {

    private interface NeumannBoundaryFunction {

        double value(double[] coord, double[] unitOutNormal);
    }

    public enum SampleCase {

        LINEAR, QUADRATIC, SIN;

        public double getSolution(double[] coord) {
            return SOLUTIONS[ordinal()].value(coord);
        }

        public double getVolumeSource(double[] coord) {
            return VOLUME_SOURCES[ordinal()].value(coord);
        }

        public double getNeumannBoundaryCondition(double[] coord, double[] unitOutNormal) {
            final double UNITY_TOL = 1e-12;
            if (abs(Math2D.dot(unitOutNormal, unitOutNormal) - 1) > UNITY_TOL) {
                throw new IllegalArgumentException();
            }
            return NEUMANN_BCS[ordinal()].value(coord, unitOutNormal);
        }
        private static final ArrvarFunction[] SOLUTIONS = new ArrvarFunction[]{
            new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    double x = vec[0];
                    double y = vec[1];
                    return x + 2 * y;
                }
            },
            new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    double x = vec[0];
                    double y = vec[1];
                    return 0.1 * x + 0.3 * y + 0.8 * x * x + 1.2 * x * y + 0.6 * y * y;
                }
            },
            new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    double x = vec[0];
                    double y = vec[1];
                    return sin(PI * x) * sin(PI * y) / (2 * PI * PI);
                }
            }
        };
        private static final ArrvarFunction[] VOLUME_SOURCES = new ArrvarFunction[]{
            new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    return 0;
                }
            },
            new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    return -2.8;
                }
            },
            new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    double x = vec[0];
                    double y = vec[1];
                    return sin(PI * x) * sin(PI * y);
                }
            }
        };
        private static final NeumannBoundaryFunction[] NEUMANN_BCS = new NeumannBoundaryFunction[]{
            new NeumannBoundaryFunction() {
                @Override
                public double value(double[] coord, double[] unitOutNormal) {
                    return unitOutNormal[0] + 2 * unitOutNormal[1];
                }
            },
            new NeumannBoundaryFunction() {
                @Override
                public double value(double[] coord, double[] unitOutNormal) {
                    double x = coord[0];
                    double y = coord[1];
                    double dx = 0.1 + 1.6 * x + 1.2 * y;
                    double dy = 0.3 * 1.2 * x + 1.2 * y;
                    return dx * unitOutNormal[0] + dy * unitOutNormal[1];
                }
            },
            new NeumannBoundaryFunction() {
                @Override
                public double value(double[] coord, double[] unitOutNormal) {
                    double x = coord[0];
                    double y = coord[1];
                    double dx = cos(PI * x) * sin(PI * y) / (2 * PI);
                    double dy = sin(PI * x) * cos(PI * y) / (2 * PI);
                    return unitOutNormal[0] * dx + unitOutNormal[1] * dy;
                }
            }
        };
    };
    SampleCase sampleCase;

    public TwoDPoissonSampleFactory(SampleCase sampleCase) {
        this.sampleCase = sampleCase;
    }
    RectangleProjectFactory rectangleProjectFactory = new RectangleProjectFactory();

    @Override
    public MFProject produce() {
        setupRetangleSize();
        setupBoundaryConditions();
        setupAssembler();
        rectangleProjectFactory.setValueDimension(1);
        return rectangleProjectFactory.produce();
    }

    private void setupRetangleSize() {
        rectangleProjectFactory.setEdgePosition(DOWN, -1);
        rectangleProjectFactory.setEdgePosition(LEFT, -1);
        rectangleProjectFactory.setEdgePosition(UP, 1);
        rectangleProjectFactory.setEdgePosition(RIGHT, 1);
    }

    private void setupBoundaryConditions() {
        AbstractSegmentLoad upLoad = new AbstractSegmentLoad() {
            @Override
            public boolean isDirichlet() {
                return true;
            }

            @Override
            public double[] getLoad() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[]{sampleCase.getSolution(coord)};
            }

            @Override
            public boolean[] getLoadValidity() {
                return new boolean[]{true};
            }

            @Override
            public boolean isSynchronizedClonable() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public MFLoad synchronizedClone() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        AbstractSegmentLoad neumannLoad = new AbstractSegmentLoad() {
            @Override
            public boolean isDirichlet() {
                return false;
            }

            @Override
            public double[] getLoad() {
                double[] coord = segment.values(parameter, null);
                double[] unitOutNormal = Segment2DUtils.chordUnitOutNormal(segment, null);
                return new double[]{sampleCase.getNeumannBoundaryCondition(coord, unitOutNormal)};
            }

            @Override
            public boolean[] getLoadValidity() {
                return null;
            }

            @Override
            public boolean isSynchronizedClonable() {
                return false;
            }

            @Override
            public MFLoad synchronizedClone() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        VolumeLoad volumeLoad = new VolumeLoad() {
            @Override
            public double[] getLoad(double[] coord) {
                double value = sampleCase.getVolumeSource(coord);
                return new double[]{value};
            }

            @Override
            public boolean isSynchronizedClonable() {
                return false;
            }

            @Override
            public MFLoad synchronizedClone() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        rectangleProjectFactory.setEdgeLoad(UP, upLoad);

        rectangleProjectFactory.setEdgeLoad(DOWN, neumannLoad);
        rectangleProjectFactory.setEdgeLoad(LEFT, neumannLoad);
        rectangleProjectFactory.setEdgeLoad(RIGHT, neumannLoad);

        rectangleProjectFactory.setVolumeLoad(volumeLoad);
    }

    private void setupAssembler() {
        PoissonVolumeAssembler posAssembler = new PoissonVolumeAssembler();
        posAssembler.setSpatialDimension(2);
        rectangleProjectFactory.setAssemblersGroup(Assemblers.poissonLagrangle());
    }

    public double getEdgePosition(MFRectangleEdge edge) {
        return rectangleProjectFactory.getEdgePosition(edge);
    }

    public double getHeight() {
        return rectangleProjectFactory.getHeight();
    }

    public int getQuadratureDegree() {
        return rectangleProjectFactory.getQuadratureDegree();
    }

    public double getWidth() {
        return rectangleProjectFactory.getWidth();
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

    public void setSpaceNodesDisturbRatio(double spaceNodesDisturbRatio) {
        rectangleProjectFactory.setSpaceNodesDisturbRatio(spaceNodesDisturbRatio);
    }

    public void setDisturbRand(Random disturbRand) {
        rectangleProjectFactory.setDisturbRand(disturbRand);
    }

    public double getSpaceNodesDisturbRatio() {
        return rectangleProjectFactory.getSpaceNodesDisturbRatio();
    }

    public static void main(String[] args) {
        SampleCase sampleCase = SampleCase.LINEAR;
        TwoDPoissonSampleFactory factory = new TwoDPoissonSampleFactory(sampleCase);
        double nodesDistance = 0.21;
        factory.setNodesDistance(nodesDistance);
        factory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(nodesDistance * 2.5));
        factory.setQuadratureDegree(1);
        MFProject project = factory.produce();

        MFLinearProcessor processor = new MFLinearProcessor();
        processor.setProject(project);

        processor.getSettings().put(MFPreprocessorKey.MULTITHREADABLE, false);
//        processor.getSettings().put(MFConstants.KEY_FORCIBLE_THREAD_NUMBER, 100);
        processor.preprocess();
        processor.solve();

        PostProcessor pp = processor.genPostProcessor();
        double[] pt = new double[]{0.5, 0.5};
        double[] value = pp.value(pt, null);
        System.out.println("value = " + value[0]);
        System.out.println("exact = " + SampleCase.LINEAR.getSolution(pt));

    }
}
