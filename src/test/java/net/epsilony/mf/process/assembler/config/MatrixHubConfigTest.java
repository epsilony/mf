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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.MFParmContainerPool;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MatrixHubConfigTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testMatriesPostAndClear() {
        @SuppressWarnings("resource")
        ApplicationContext ac = new AnnotationConfigApplicationContext(AssemblerBaseConfig.class,
                MockAssemblerConfig.class);
        MFParmContainerPool parmContainerPool = MFParmContainerPool.fromApplicationContext(ac);
        int groupSize = 4;
        int valueNodesNum = 10;
        int lagrangleNodesNum = 3;

        for (int i = 0; i < groupSize; i++) {
            ac.getBean(AssemblerBaseConfig.ASSEMBLERS_GROUP_PROTO, AssemblersGroup.class);
        }

        List<AssemblersGroup> groups = (List<AssemblersGroup>) ac.getBean(AssemblerBaseConfig.ASSEMBLERS_GROUPS);

        assertEquals(groupSize, groups.size());

        ArrayList<MFNode> valueNodes = genNewNodes(valueNodesNum);
        ArrayList<MFNode> lagrangleNodes = genNewNodes(lagrangleNodesNum);

        int valueDimension = 2;
        int spatialDimension = 2;

        parmContainerPool.setOpenParm("nodes", valueNodes);
        parmContainerPool.setOpenParm("lagrangleDirichletNodes", lagrangleNodes);
        parmContainerPool.setOpenParm("valueDimension", valueDimension);
        parmContainerPool.setOpenParm("spatialDimension", spatialDimension);
        MatrixHub matrixHub = ac.getBean(MatrixHub.class);
        matrixHub.post();

        Set<MFMatrix> allPostedMatries = new HashSet<>();
        Set<MFMatrix> allPostedVectors = new HashSet<>();
        groups.stream().forEach(
                (group) -> {
                    Arrays.asList(group.getVolume(), group.getVolumeLoad(), group.getDirichlet(), group.getNeumann())
                            .stream().forEach((asm) -> {
                                MockAssembler mock = (MockAssembler) asm;
                                allPostedMatries.add(mock.getMainMatrix());
                                allPostedVectors.add(mock.getMainVector());
                            });
                });
        assertEquals(groupSize, allPostedMatries.size());
        assertEquals(groupSize, allPostedVectors.size());

        allPostedMatries.stream().forEach((mat) -> {
            assertEquals(valueDimension * (valueNodesNum + lagrangleNodesNum), mat.numCols());
            assertEquals(valueDimension * (valueNodesNum + lagrangleNodesNum), mat.numRows());
        });

        allPostedVectors.stream().forEach((mat) -> {
            assertEquals(valueDimension * (valueNodesNum + lagrangleNodesNum), mat.numRows());
            assertEquals(1, mat.numCols());
        });

        matrixHub.mergePosted();
        matrixHub.clearPosted();
        allPostedMatries.clear();
        allPostedVectors.clear();
        groups.stream().forEach(
                (group) -> {
                    Arrays.asList(group.getVolume(), group.getVolumeLoad(), group.getDirichlet(), group.getNeumann())
                            .stream().forEach((asm) -> {
                                MockAssembler mock = (MockAssembler) asm;
                                allPostedMatries.add(mock.getMainMatrix());
                                allPostedVectors.add(mock.getMainVector());
                            });
                });
        allPostedMatries.remove(null);
        allPostedVectors.remove(null);
        assertTrue(allPostedMatries.isEmpty());
        assertTrue(allPostedVectors.isEmpty());
    }

    private ArrayList<MFNode> genNewNodes(int size) {
        ArrayList<MFNode> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new MFNode());
        }
        return result;
    }

    public static class MockAssembler implements LagrangleAssembler {
        MFMatrix mainMatrix, mainVector;

        @Override
        public void assemble() {

        }

        @Override
        public void setAssemblyInput(AssemblyInput assemblyInput) {

        }

        public MFMatrix getMainMatrix() {
            return mainMatrix;
        }

        @Override
        public void setMainMatrix(MFMatrix mainMatrix) {
            this.mainMatrix = mainMatrix;
        }

        public MFMatrix getMainVector() {
            return mainVector;
        }

        @Override
        public void setMainVector(MFMatrix mainVector) {
            this.mainVector = mainVector;
        }

        @Override
        public void setValueDimension(int valueDimension) {

        }

        @Override
        public void setSpatialDimension(int spatialDimension) {

        }

        @Override
        public void setLagrangleNodesNum(int size) {

        }

    }

    @Configuration
    public static class MockAssemblerConfig {

        @Resource(name = "assemblerBaseParmContainer")
        MFParmContainer assemblerBaseParmContainer;

        @Bean(name = AssemblerBaseConfig.DIRICHLET_ASSEMBLER_PROTO)
        @Scope("prototype")
        Assembler dirichletAssemblerProto() {
            LagrangleAssembler result = new MockAssembler();
            assemblerBaseParmContainer.register("nodes",
                    (LagrangleAssembler obj, List<MFNode> nodes) -> obj.setLagrangleNodesNum(nodes.size()), result);
            return result;
        }

        @Bean(name = AssemblerBaseConfig.NEUMANN_ASSEMBLER_PROTO)
        @Scope("prototype")
        Assembler neumannAssemblerProto() {
            return new MockAssembler();
        }

        @Bean(name = AssemblerBaseConfig.VOLUME_ASSEMBLER_PROTO)
        @Scope("prototype")
        Assembler volumeAssemblerProto() {
            return new MockAssembler();
        }
    }
}
