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
package net.epsilony.mf.process.assembler.config;

import java.util.ArrayList;
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.matrix.LagrangleDiagCompatibleMatrixMerger;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.process.assembler.matrix.MatrixMerger;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.matrix.AutoMFMatrixFactory;
import net.epsilony.mf.util.matrix.AutoSparseMatrixFactory;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.RelayParmContainerBuilder;
import net.epsilony.mf.util.parm.TriggerParmToBusSwitcher;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import no.uib.cipr.matrix.DenseVector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class AssemblerBaseConfig extends ApplicationContextAwareImpl {
    // need to config:-------------------
    public static final String VOLUME_ASSEMBLER_PROTO    = "volumeAssemblerProto";
    public static final String NEUMANN_ASSEMBLER_PROTO   = "neumannAssemblerProto";
    public static final String DIRICHLET_ASSEMBLER_PROTO = "dirichletAssemblerProto";
    // end of need to config ----------------------

    // optional
    public static final String MAIN_MATRIX_FACTORY       = "mainMatrixFactory";
    public static final String MAIN_VECTOR_FACTORY       = "mainVectorFactory";
    // end of optional

    public static final String ASSEMBLERS_GROUP_PROTO    = "assemblersGroupProto";
    public static final String ASSEMBLERS_GROUPS         = "assemblersGroups";

    @Bean
    public MFParmContainer assemblerBaseParmContainer() {
        return new RelayParmContainerBuilder().addParms("spatialDimension", "valueDimension", "nodes",
                "lagrangleDirichletNodes").get();
    }

    @Bean(name = ASSEMBLERS_GROUP_PROTO)
    @Scope("prototype")
    public AssemblersGroup assemblersGroupProto() {
        AssemblersGroup result = new AssemblersGroup(
                applicationContext.getBean(VOLUME_ASSEMBLER_PROTO, Assembler.class), applicationContext.getBean(
                        NEUMANN_ASSEMBLER_PROTO, Assembler.class), applicationContext.getBean(NEUMANN_ASSEMBLER_PROTO,
                        Assembler.class), applicationContext.getBean(DIRICHLET_ASSEMBLER_PROTO, Assembler.class));
        assemblersGroups().add(result);
        assemblerBaseParmContainer().autoRegister(result);

        mainMatrixBus().register(AssemblersGroup::setMainMatrix, result);
        mainVectorBus().register(AssemblersGroup::setMainVector, result);

        return result;
    }

    @Bean(name = ASSEMBLERS_GROUPS)
    public List<AssemblersGroup> assemblersGroups() {
        return new ArrayList<>();
    }

    public static final String MAIN_MATRIX_BUS = "mainMatrixBus";
    public static final String MAIN_VECTOR_BUS = "mainVectorBus";

    @Bean(name = MAIN_MATRIX_BUS)
    public WeakBus<MFMatrix> mainMatrixBus() {
        return new WeakBus<>(MAIN_MATRIX_BUS);
    }

    @Bean(name = MAIN_VECTOR_BUS)
    public WeakBus<MFMatrix> mainVectorBus() {
        return new WeakBus<>(MAIN_VECTOR_BUS);
    }

    public static final String MATRIX_HUB = "matrixHub";

    @Bean(name = MATRIX_HUB)
    public MatrixHub matrixHub() {
        MatrixHub matrixHub = new MatrixHub();
        matrixHub.setMainMatrixBus(mainMatrixBus());
        matrixHub.setMainVectorBus(mainVectorBus());
        matrixHub.setMainMatrixFactory(getMainMatrixFactory());
        matrixHub.setMainVectorFactory(getMainVectorFactory());

        TriggerParmToBusSwitcher switcher = assemblerBaseParmContainer().parmToBusSwitcher();

        switcher.register("nodes", (obj, values) -> {
            List<MFNode> nodes = (List<MFNode>) values;
            obj.setValueNodesNum(nodes.size());
        }, matrixHub);

        switcher.register("lagrangleDirichletNodes", (obj, value) -> {
            List<MFNode> lagNodes = (List<MFNode>) value;
            obj.setLagrangleNodesNum(lagNodes.size());
        }, matrixHub);

        switcher.register("valueDimension", matrixHub);

        matrixHub.setMatrixMerger(matrixMerger());
        return matrixHub;
    }

    public static final String MATRIX_MERGER = "matrixMerger";

    @Bean
    MatrixMerger matrixMerger() {
        return new LagrangleDiagCompatibleMatrixMerger();
    }

    @SuppressWarnings("unchecked")
    private MatrixFactory<? extends MFMatrix> getMainMatrixFactory() {
        if (applicationContext.containsBean(MAIN_MATRIX_FACTORY)) {
            return (MatrixFactory<? extends MFMatrix>) applicationContext.getBean(MAIN_MATRIX_FACTORY);
        } else {
            return AutoSparseMatrixFactory.produceDefault();
        }
    }

    @SuppressWarnings("unchecked")
    private MatrixFactory<? extends MFMatrix> getMainVectorFactory() {
        if (applicationContext.containsBean(MAIN_MATRIX_FACTORY)) {
            return (MatrixFactory<? extends MFMatrix>) applicationContext.getBean(MAIN_VECTOR_FACTORY);
        } else {
            return new AutoMFMatrixFactory(DenseVector.class);
        }
    }
}
