/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.MFProcessType;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegrateCores {

    public static Map<MFProcessType, MFIntegratorCore> commonCoresGroup() {
        EnumMap<MFProcessType, MFIntegratorCore> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new SimpVolumeMFIntegratorCore());
        result.put(MFProcessType.NEUMANN, new SimpNeumannIntegratorCore());
        result.put(MFProcessType.DIRICHLET, new SimpDirichletIntegratorCore());
        return result;
    }
}
