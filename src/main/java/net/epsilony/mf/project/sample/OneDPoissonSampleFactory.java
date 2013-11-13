/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.project.sample;

import java.util.Arrays;

import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.model.load.ConstantNodeLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.MFPreprocessorKey;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.indexer.OneDChainLagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
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

    static final UnivariateFunction[] volumeLoads = new UnivariateFunction[] { new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 0;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 8;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 8 + 16 * x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x);
        }
    } };
    static final double[][] startEndDirichlets = new double[][] { { 0, 1 }, { 0, 1 }, { 0, 1 }, { 0, 0 } };
    static final UnivariateFunction[] solutions = new UnivariateFunction[] { new UnivariateFunction() {
        @Override
        public double value(double x) {
            return x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return -4 * x * x + 5 * x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return -8.0 / 3 * x * x * x - 4 * x * x + 23.0 / 3 * x;
        }
    }, new UnivariateFunction() {
        @Override
        public double value(double x) {
            return Math.sin(Math.PI * 2 * x);
        }
    } };
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
        ConstantNodeLoad result = new ConstantNodeLoad();
        result.setValue(new double[] { load });
        result.setValidity(new boolean[] { true });
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
            public double[] getValue() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[] { volumeLoad.value(coord[0]) };
            }

            @Override
            public boolean[] getValidity() {
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
        Choice choice = Choice.CONSTANT;
        OneDPoissonSampleFactory sampleProject = new OneDPoissonSampleFactory(choice);
        MFLinearProcessor processor = new MFLinearProcessor();
        processor.setNodesAssembleIndexer(new OneDChainLagrangleNodesAssembleIndexer());
        // processor.getSettings().put(MFConstants.KEY_FORCIBLE_THREAD_NUMBER,
        // 25);
        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setThreadNum(1);
        processor.getSettings().put(MFPreprocessorKey.INTEGRATOR, factory.produce());
        processor.setProject(sampleProject.produce());
        processor.preprocess();
        // IntegrateResult integrateResult = processor.getIntegrateResult();
        // System.out.println(integrateResult.getMainMatrix());
        processor.solve();
        PostProcessor postProcessor = processor.genPostProcessor();
        double[] samplePoints = new double[] { 0, 0.1, 0.2, 0.5, 0.7, 1 };
        for (double sp : samplePoints) {
            double[] act = postProcessor.value(new double[] { sp, 0 }, null);
            double exp = choice.getSolution().value(sp);
            System.out.println("sp = " + sp);
            System.out.println("act[0] = " + act[0]);
            System.out.println("exp = " + exp);
            System.out.println("");
        }
    }
}
