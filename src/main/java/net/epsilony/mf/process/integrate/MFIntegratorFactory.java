/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegrateCores;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.AutoSparseMatrixFactory;
import net.epsilony.mf.process.integrate.observer.CounterIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.HashRowMatrix;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.tb.Factory;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegratorFactory implements Factory<MFIntegrator> {

    Integer threadNum = null;
    Map<MFProcessType, MFIntegratorCore> coresGroup = null;
    Set<MFIntegratorObserver> observers = new HashSet<>();
    MatrixFactory denseMainMatrixFactory;
    MatrixFactory sparseMainMatrixFactory;
    MatrixFactory mainVectorFactory;

    public MFIntegratorFactory() {
        observers.add(new CounterIntegratorObserver());
    }

    public Map<MFProcessType, MFIntegratorCore> getCoresGroup() {
        return coresGroup;
    }

    public void setCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup) {
        this.coresGroup = coresGroup;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    @Override
    public MFIntegrator produce() {
        MFIntegrator integrator = byThreadsNum();
        integrator.setIntegratorCoresGroup(genCoresGroup());
        integrator.addObservers(observers);
        fillMainMatrixVectorFactories(integrator);
        return integrator;
    }

    private MFIntegrator byThreadsNum() {
        if (threadNum == null || threadNum > 1) {
            return new MultithreadMFIntegrator(threadNum);
        } else {
            return new SimpMFIntegrator();
        }
    }

    private Map<MFProcessType, MFIntegratorCore> genCoresGroup() {
        if (coresGroup != null) {
            return coresGroup;
        } else {
            return MFIntegrateCores.commonCoresGroup();
        }
    }

    public boolean addObserver(MFIntegratorObserver e) {
        return observers.add(e);
    }

    private void fillMainMatrixVectorFactories(MFIntegrator integrator) {
        AutoSparseMatrixFactory matrixFactory = new AutoSparseMatrixFactory();
        matrixFactory.setDenseMatrixFactory(denseMainMatrixFactory == null ? defaultDenseMainMatrixFactory() : denseMainMatrixFactory);
        matrixFactory.setSparseMatrixFactory(sparseMainMatrixFactory == null ? defaultSparseMainMatrixFactory() : sparseMainMatrixFactory);
        integrator.setMainMatrixFactory(matrixFactory);

        integrator.setMainVectorFactory(mainVectorFactory == null ? defaultMainVectorFactory() : mainVectorFactory);
    }

    private MatrixFactory<? extends MFMatrix> defaultDenseMainMatrixFactory() {
        return new AutoMFMatrixFactory(DenseMatrix.class);
    }

    private MatrixFactory<? extends MFMatrix> defaultSparseMainMatrixFactory() {
        return new AutoMFMatrixFactory(HashRowMatrix.class);
    }

    private MatrixFactory<? extends MFMatrix> defaultMainVectorFactory() {
        return new AutoMFMatrixFactory(DenseVector.class);
    }

    public void setDenseMainMatrixFactory(MatrixFactory denseMainMatrixFactory) {
        this.denseMainMatrixFactory = denseMainMatrixFactory;
    }

    public void setSparseMainMatrixFactory(MatrixFactory sparseMainMatrixFactory) {
        this.sparseMainMatrixFactory = sparseMainMatrixFactory;
    }

    public void setMainVectorFactory(MatrixFactory mainVectorFactory) {
        this.mainVectorFactory = mainVectorFactory;
    }
}
