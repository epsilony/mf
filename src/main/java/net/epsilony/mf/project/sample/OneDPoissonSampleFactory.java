/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.util.Arrays;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.project.OneDPoissonProjectFactory;
import net.epsilony.tb.Factory;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoissonSampleFactory implements Factory<MFProject> {

    public static final int START_COORD = 0, END_COORD = 1;

    public enum Choice {

        ZERO, CONSTANT, LINEAR, TRIGONOMETRIC;

        UnivariateFunction getVolumeLoad() {
            return volumeLoads[ordinal()];
        }
//

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
    Choice choice;
    OneDPoissonProjectFactory oneDPoissonProjectFactory = new OneDPoissonProjectFactory();

    @Override
    public MFProject produce() {
        oneDPoissonProjectFactory.setStart(START_COORD);
        oneDPoissonProjectFactory.setEnd(END_COORD);

        oneDPoissonProjectFactory.setStartLoad(genStartEndLoad(true));
        oneDPoissonProjectFactory.setEndLoad(genStartEndLoad(false));
        oneDPoissonProjectFactory.setVolumeLoad(genVolumeLoad());

        return oneDPoissonProjectFactory.produce();

    }

    private NodeLoad genStartEndLoad(boolean start) {
        double load = choice.getStartEndDirichlet()[start ? 0 : 1];
        NodeLoad result = new NodeLoad();
        result.setLoad(new double[]{load});
        result.setLoadValidity(new boolean[]{true});
        return result;
    }

    private SegmentLoad genVolumeLoad() {

        SegmentLoad result;
        result = new AbstractSegmentLoad() {
            UnivariateFunction volumeLoad = choice.getVolumeLoad();

            @Override
            public boolean isDirichlet() {
                return false;
            }

            @Override
            public double[] getLoad() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[]{volumeLoad.value(coord[0])};
            }

            @Override
            public boolean[] getLoadValidity() {
                return null;
            }
        };
        return result;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public OneDPoissonSampleFactory(Choice choice) {
        this.choice = choice;
    }

    public OneDPoissonSampleFactory() {
    }

    public int getNodesNum() {
        return oneDPoissonProjectFactory.getNodesNum();
    }

    public void setNodesNum(int nodesNum) {
        oneDPoissonProjectFactory.setNodesNum(nodesNum);
    }

    public int getQuadratureDegree() {
        return oneDPoissonProjectFactory.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        oneDPoissonProjectFactory.setQuadratureDegree(quadratureDegree);
    }

    public double getInfluenceRadRatio() {
        return oneDPoissonProjectFactory.getInfluenceRadRatio();
    }

    public void setInfluenceRadRatio(double influenceRadRatio) {
        oneDPoissonProjectFactory.setInfluenceRadRatio(influenceRadRatio);
    }

    public static void main(String[] args) {
        OneDPoissonSampleFactory sample = new OneDPoissonSampleFactory(Choice.LINEAR);
        MFLinearProcessor processor = new MFLinearProcessor();
        processor.setProject(sample.produce());
        processor.preprocess();
    }
}