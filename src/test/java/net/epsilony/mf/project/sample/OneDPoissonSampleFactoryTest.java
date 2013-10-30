/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.MFPreprocessorKey;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
import net.epsilony.mf.project.sample.OneDPoissonSampleFactory.Choice;
import net.epsilony.tb.TestTool;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoissonSampleFactoryTest {

    public OneDPoissonSampleFactoryTest() {
    }
    int nodesNum = 21;
    double errLimit = 5e-3;
    int samplePointNum = 100;

    @Test
    public void testSamples() {
        for (Choice choice : Choice.values()) {
            testChoice(choice);
        }
    }

    public void testChoice(Choice choice) {
        OneDPoissonSampleFactory sampleProject = new OneDPoissonSampleFactory(choice);
        sampleProject.setNodesNum(nodesNum);
        MFLinearProcessor processor = new MFLinearProcessor();
//        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setThreadNum(5);
        processor.getSettings().put(MFPreprocessorKey.INTEGRATOR, factory.produce());
        processor.setProject(sampleProject.produce());
        processor.preprocess();
        processor.solve();
        PostProcessor postProcessor = processor.genPostProcessor();
        double[] samplePoints = genSamplePoints();
        double[] coord = new double[2];
        UnivariateFunction solution = choice.getSolution();
        double errSum = 0;
        int errSumNum = 0;
        for (double point : samplePoints) {
            double exp = solution.value(point);
            if (exp < 1e-7) {
                continue;
            }
            coord[0] = point;
            double act = postProcessor.value(coord, null)[0];
            errSum += FastMath.abs(exp - act) / act;
            errSumNum++;
        }
        double avgErr = errSum / errSumNum;
        System.out.println("avgErr = " + avgErr);
        System.out.println("errSumNum = " + errSumNum);
        assertTrue(errSumNum > 0);
        assertTrue(avgErr < errLimit);
    }

    private double[] genSamplePoints() {
        return TestTool.linSpace(OneDPoissonSampleFactory.START_COORD, OneDPoissonSampleFactory.END_COORD, samplePointNum);
    }
}
