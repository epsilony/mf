/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MultithreadMFIntegrator;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.util.MFKey;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.HashRowMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFPreprocessorKey implements MFKey {

    INTEGRATOR(MFIntegrator.class),
    MAIN_MATRIX_SOLVER(MFSolver.class),
    DENSE_MAIN_MATRIX_FACTORY(MatrixFactory.class),
    SPARSE_MAIN_MATRIX_FACTORY(MatrixFactory.class),
    MAIN_VECTOR_FACTORY(MatrixFactory.class);

    private MFPreprocessorKey(Class<?> valueType) {
        this.valueType = valueType;
    }

    private final Class<?> valueType;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }

    public static Map<MFKey, Object> getDefaultSettings() {
        Map<MFKey, Object> result = new HashMap<>();
        result.put(INTEGRATOR, new MultithreadMFIntegrator());
        result.put(MAIN_MATRIX_SOLVER, new RcmSolver());
        result.put(DENSE_MAIN_MATRIX_FACTORY, new AutoMFMatrixFactory(DenseMatrix.class));
        result.put(SPARSE_MAIN_MATRIX_FACTORY, new AutoMFMatrixFactory(HashRowMatrix.class));
        result.put(MAIN_VECTOR_FACTORY, new AutoMFMatrixFactory(DenseVector.class));
        return result;
    }

}
