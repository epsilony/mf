/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.util.MFKey;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFPreprocessorKey implements MFKey {

    MULTITHREADABLE(Boolean.class),
    THREADS_NUM(Integer.class),
    MAIN_MATRIX_SOLVER(MFSolver.class);

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
        result.put(MULTITHREADABLE, true);
        result.put(MAIN_MATRIX_SOLVER, new RcmSolver());
        return result;
    }

}
