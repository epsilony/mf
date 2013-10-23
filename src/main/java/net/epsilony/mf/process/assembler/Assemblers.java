/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.MFProcessType;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Assemblers {

    public static Map<MFProcessType, Assembler> mechanicalLagrangle() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new MechanicalVolumeAssembler());
        result.put(MFProcessType.NEUMANN, new NeumannAssembler());
        result.put(MFProcessType.DIRICHLET, new LagrangleDirichletAssembler());
        return result;
    }

    public static Map<MFProcessType, Assembler> poissonLagrangle() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new PoissonVolumeAssembler());
        result.put(MFProcessType.NEUMANN, new NeumannAssembler());
        result.put(MFProcessType.DIRICHLET, new LagrangleDirichletAssembler());
        return result;
    }
}
