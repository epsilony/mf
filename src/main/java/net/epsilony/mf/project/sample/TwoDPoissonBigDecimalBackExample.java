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
import net.epsilony.mf.process.assembler.matrix_merge.EmptyMatrixMerger;
import net.epsilony.mf.process.assembler.matrix_merge.UrglySingleMatrixMultiThreadMerger;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
import net.epsilony.mf.process.integrate.MultithreadMFIntegrator;
import net.epsilony.mf.process.integrate.observer.CoreRecorderObserver;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.util.MFKey;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.BigDecimalDenseMatrix;
import net.epsilony.mf.util.matrix.BigDecimalMFMatrix;
import net.epsilony.mf.util.matrix.BigDecimalTreeMapRowMatrix;
import net.epsilony.mf.util.matrix.SingleSynchronziedBigDecimalMatrixFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TwoDPoissonBigDecimalBackExample {

    TwoDPoissonSampleFactory.SampleCase sampleCase = TwoDPoissonSampleFactory.SampleCase.LINEAR;
    double nodesDistance = 0.21;
    InfluenceRadiusCalculator influenceRadiusCalculator = new ConstantInfluenceRadiusCalculator(nodesDistance * 2.5);
    int quadratureDegree = 2;
    int threadsNum = 1;
    PostProcessor postProcessor;
    MFProject project;
    MFLinearProcessor processor;
    boolean useSingleMatrixVector = false;
    boolean recordCore = false;

    public void prepare() {
        project = produceProject();
        processor = produceProcessor();
        processor.setProject(project);
    }

    public void processAndSolve() {
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
        MFIntegrator integrator = produceIntegrator();
        settings.put(MFPreprocessorKey.INTEGRATOR, integrator);
        return result;
    }

    private MFIntegrator produceIntegrator() {

        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setThreadNum(threadsNum);
        MFIntegrator integrator;
        if (useSingleMatrixVector) {
            factory.setDenseMainMatrixFactory(new SingleSynchronziedBigDecimalMatrixFactory(BigDecimalDenseMatrix.class));
            factory.setSparseMainMatrixFactory(new SingleSynchronziedBigDecimalMatrixFactory(BigDecimalTreeMapRowMatrix.class));
            factory.setMainVectorFactory(new SingleSynchronziedBigDecimalMatrixFactory(BigDecimalDenseMatrix.class));

            integrator = factory.produce();
            if (integrator instanceof MultithreadMFIntegrator) {
                MultithreadMFIntegrator mtIntegrator = (MultithreadMFIntegrator) integrator;
                mtIntegrator.setMainMatrixMerger(new UrglySingleMatrixMultiThreadMerger());
                mtIntegrator.setMainVectorMerger(new EmptyMatrixMerger());
            }
        } else {
            factory.setDenseMainMatrixFactory(new AutoMFMatrixFactory(BigDecimalDenseMatrix.class));
            factory.setSparseMainMatrixFactory(new AutoMFMatrixFactory(BigDecimalTreeMapRowMatrix.class));
            factory.setMainVectorFactory(new AutoMFMatrixFactory(BigDecimalDenseMatrix.class));

            integrator = factory.produce();
        }
        if (recordCore) {
            integrator.addObserver(new CoreRecorderObserver());
        }
        return integrator;

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

    public void setUseSingleMatrixVector(boolean useSingleMatrixVector) {
        this.useSingleMatrixVector = useSingleMatrixVector;
    }

    public boolean isRecordCore() {
        return recordCore;
    }

    public void setRecordCore(boolean recordCore) {
        this.recordCore = recordCore;
    }

    public static TwoDPoissonBigDecimalBackExample singleThread(TwoDPoissonSampleFactory.SampleCase sampleCase) {
        TwoDPoissonBigDecimalBackExample result = new TwoDPoissonBigDecimalBackExample();
        result.setSampleCase(sampleCase);
        result.setRecordCore(true);
        result.prepare();
        result.processAndSolve();
        return result;
    }

    public static TwoDPoissonBigDecimalBackExample multiThread(TwoDPoissonSampleFactory.SampleCase sampleCase) {
        int threadNum = 10;
        TwoDPoissonBigDecimalBackExample result = new TwoDPoissonBigDecimalBackExample();
        result.setSampleCase(sampleCase);
        result.setThreadsNum(threadNum);
        result.setRecordCore(true);
        result.prepare();
        result.processAndSolve();
        return result;
    }

    public static TwoDPoissonBigDecimalBackExample multiThreadOneMatrix(TwoDPoissonSampleFactory.SampleCase sampleCase) {
        TwoDPoissonBigDecimalBackExample result = new TwoDPoissonBigDecimalBackExample();
        result.setSampleCase(sampleCase);
        result.setThreadsNum(10);
        result.setUseSingleMatrixVector(true);
        result.setRecordCore(true);
        result.prepare();
        result.processAndSolve();
        return result;
    }

    public static void compareSingleMulti() {
        TwoDPoissonSampleFactory.SampleCase sampleCase = TwoDPoissonSampleFactory.SampleCase.LINEAR;
        TwoDPoissonBigDecimalBackExample singleThreadData = singleThread(sampleCase);
        System.out.println("------------------------------------------");
        TwoDPoissonBigDecimalBackExample multiThreadData = multiThread(sampleCase);
        double[] coord = new double[]{0.5, 0.5};
        double[] singleValue = singleThreadData.getPostProcessor().value(coord, null);
        double[] multiValue = multiThreadData.getPostProcessor().value(coord, null);
        System.out.println("singleValue = " + Arrays.toString(singleValue));
        System.out.println("multiValue = " + Arrays.toString(multiValue));
        BigDecimalMFMatrix singleThreadMainMatrix = (BigDecimalMFMatrix) singleThreadData.getProcessor().getIntegrateResult().getMainMatrix();
        BigDecimalMFMatrix multiThreadMainMatrix = (BigDecimalMFMatrix) multiThreadData.getProcessor().getIntegrateResult().getMainMatrix();

        compareMatries(singleThreadMainMatrix, multiThreadMainMatrix);
    }

    public static void compareSingleToSMS() {
        TwoDPoissonSampleFactory.SampleCase sampleCase = TwoDPoissonSampleFactory.SampleCase.LINEAR;
        TwoDPoissonBigDecimalBackExample singleThreadData = singleThread(sampleCase);
        System.out.println("------------------------------------------");
        TwoDPoissonBigDecimalBackExample multiThreadData = multiThreadOneMatrix(sampleCase);
        double[] coord = new double[]{0.5, 0.5};
        double[] singleValue = singleThreadData.getPostProcessor().value(coord, null);
        double[] multiValue = multiThreadData.getPostProcessor().value(coord, null);
        System.out.println("singleValue = " + Arrays.toString(singleValue));
        System.out.println("multiValue = " + Arrays.toString(multiValue));
        BigDecimalMFMatrix singleThreadMainMatrix = (BigDecimalMFMatrix) singleThreadData.getProcessor().getIntegrateResult().getMainMatrix();
        BigDecimalMFMatrix multiThreadMainMatrix = (BigDecimalMFMatrix) multiThreadData.getProcessor().getIntegrateResult().getMainMatrix();

        compareMatries(singleThreadMainMatrix, multiThreadMainMatrix);
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

    public static void main(String[] args) {
        compareSingleMulti();
    }
}
