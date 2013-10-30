/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.MFPreprocessorKey;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.util.MFKey;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.BigDecimalDenseMatrix;
import net.epsilony.mf.util.matrix.BigDecimalMFMatrix;
import net.epsilony.mf.util.matrix.BigDecimalTreeMapRowMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TwoDPoissonBigDecimalBackExample {

    TwoDPoissonSampleFactory.SampleCase sampleCase;
    double nodesDistance = 0.21;
    InfluenceRadiusCalculator influenceRadiusCalculator = new ConstantInfluenceRadiusCalculator(nodesDistance * 2.5);
    int quadratureDegree = 2;
    int threadsNum = 1;
    private PostProcessor postProcessor;
    private MFProject project;
    private MFLinearProcessor processor;

    public void processAndSolve() {
        project = produceProject();
        processor = produceProcessor();
        processor.setProject(project);
        processor.preprocess();
        processor.solve();
        postProcessor = processor.genPostProcessor();
    }

    public MFProject produceProject() {
        TwoDPoissonSampleFactory factory = new TwoDPoissonSampleFactory(sampleCase);
        factory.setNodesDistance(nodesDistance);
        factory.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        factory.setQuadratureDegree(quadratureDegree);
        MFProject result = factory.produce();
        return result;
    }

    public MFLinearProcessor produceProcessor() {
        MFLinearProcessor result = new MFLinearProcessor();
        Map<MFKey, Object> settings = result.getSettings();
        settings.put(MFPreprocessorKey.DENSE_MAIN_MATRIX_FACTORY, new AutoMFMatrixFactory(BigDecimalDenseMatrix.class));
        settings.put(MFPreprocessorKey.SPARSE_MAIN_MATRIX_FACTORY, new AutoMFMatrixFactory(BigDecimalTreeMapRowMatrix.class));
        settings.put(MFPreprocessorKey.MAIN_VECTOR_FACTORY, new AutoMFMatrixFactory(BigDecimalDenseMatrix.class));
        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setThreadNum(threadsNum);
        settings.put(MFPreprocessorKey.INTEGRATOR, factory.produce());
        return result;
    }

    public MFProject getProject() {
        return project;
    }

    public MFLinearProcessor getProcessor() {
        return processor;
    }

    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }

    public void setSampleCase(TwoDPoissonSampleFactory.SampleCase sampleCase) {
        this.sampleCase = sampleCase;
    }

    public TwoDPoissonSampleFactory.SampleCase getSampleCase() {
        return sampleCase;
    }

    public void setNodesDistance(double nodesDistance) {
        this.nodesDistance = nodesDistance;
    }

    public double getNodesDistance() {
        return nodesDistance;
    }

    public int getThreadsNum() {
        return threadsNum;
    }

    public void setThreadsNum(int threadsNum) {
        this.threadsNum = threadsNum;
    }

    public static void main(String[] args) {
        TwoDPoissonSampleFactory.SampleCase sampleCase = TwoDPoissonSampleFactory.SampleCase.LINEAR;
        TwoDPoissonBigDecimalBackExample singleThread = new TwoDPoissonBigDecimalBackExample();
        singleThread.setSampleCase(sampleCase);
        singleThread.processAndSolve();
        PostProcessor singleThreadPP = singleThread.getPostProcessor();
        System.out.println("------------------------------------------");
        TwoDPoissonBigDecimalBackExample multiThread = new TwoDPoissonBigDecimalBackExample();
        multiThread.setSampleCase(sampleCase);
        multiThread.setThreadsNum(2);
        multiThread.processAndSolve();
        PostProcessor multiThreadPP = multiThread.getPostProcessor();

        double[] coord = new double[]{0.5, 0.5};
        double[] singleValue = singleThreadPP.value(coord, null);
        double[] multiValue = multiThreadPP.value(coord, null);
        System.out.println("singleValue = " + Arrays.toString(singleValue));
        System.out.println("multiValue = " + Arrays.toString(multiValue));

        compareMatries((BigDecimalMFMatrix) singleThread.getProcessor().getIntegrateResult().getMainMatrix(), (BigDecimalMFMatrix) multiThread.getProcessor().getIntegrateResult().getMainMatrix());
    }

    public static void compareMatries(BigDecimalMFMatrix first, BigDecimalMFMatrix second) {
        System.out.println("first.numRows = " + first.numRows());
        System.out.println("first.numCols = " + first.numCols());
        int num = 0;
        for (int i = 0; i < first.numRows(); i++) {
            for (int j = 0; j < first.numCols(); j++) {
                BigDecimal firstValue = first.getBigDecimal(i, j);
                BigDecimal secondValue = second.getBigDecimal(i, j);
                if (firstValue.compareTo(secondValue) == 0) {
                    continue;
                }
                System.out.println("i,j = " + i + ", " + j);
                System.out.println("firstValue = " + firstValue);
                System.out.println("secondValue = " + secondValue);
                num++;
            }
        }
        System.out.println("num = " + num);
    }
}
