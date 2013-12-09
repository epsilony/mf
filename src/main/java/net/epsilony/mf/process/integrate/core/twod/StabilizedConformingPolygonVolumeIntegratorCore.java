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
package net.epsilony.mf.process.integrate.core.twod;

import gnu.trove.list.array.TIntArrayList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.tool.LinearQuadratureSupport;
import net.epsilony.mf.process.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.tb.analysis.Math2D;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class StabilizedConformingPolygonVolumeIntegratorCore extends AbstractMFIntegratorCore {
    public static final int SPATIAL_DIMENSION = 2;
    protected final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();
    private Assembler loadAssembler;
    private Assembler matrixAssembler;
    private final LinkedHashMap<Integer, double[]> indexValueMap = new LinkedHashMap<>();

    public StabilizedConformingPolygonVolumeIntegratorCore() {
        super();
        processType = MFProcessType.VOLUME;
    }

    @Override
    public void integrate() {

        integrateMatrix();
        integrateLoad();

    }

    private void integrateMatrix() {
        mixer.setDiffOrder(0);
        PolygonIntegrateUnit polygonIntegrateUnit = (PolygonIntegrateUnit) integrateUnit;
        double polyArea = polygonIntegrateUnit.calcArea();
        indexValueMap.clear();
        for (int vertexI = 0; vertexI < polygonIntegrateUnit.getVertesSize(); vertexI++) {
            double[] startCoord = polygonIntegrateUnit.getVertexCoord(vertexI);
            double[] endCoord = polygonIntegrateUnit.getVertexCoord((vertexI + 1)
                    % polygonIntegrateUnit.getVertesSize());
            linearQuadratureSupport.setStartEndCoords(startCoord, endCoord);
            linearQuadratureSupport.reset();
            double[] outNorm = Math2D.vectorUnitOutNormal(Math2D.subs(endCoord, startCoord, null), null);
            while (linearQuadratureSupport.hasNext()) {
                linearQuadratureSupport.next();
                double[] coord = linearQuadratureSupport.getLinearCoord();
                mixer.setCenter(coord);

                mixer.setBoundary(null);
                mixer.setUnitOutNormal(outNorm);
                MixResult mixResult = mixer.mix();
                TIntArrayList nodesAssemblyIndes = mixResult.getNodesAssemblyIndes();
                double[][] shapeFunctionValues = mixResult.getShapeFunctionValues();
                for (int nodeI = 0; nodeI < nodesAssemblyIndes.size(); nodeI++) {
                    Integer asmIndex = nodesAssemblyIndes.getQuick(nodeI);
                    double[] value = indexValueMap.get(asmIndex);
                    if (null == value) {
                        value = new double[2];
                        indexValueMap.put(asmIndex, value);
                    }
                    for (int dim = 0; dim < SPATIAL_DIMENSION; dim++) {
                        value[dim] += shapeFunctionValues[0][nodeI] * outNorm[dim]
                                * linearQuadratureSupport.getLinearWeight() / polyArea;
                    }
                }
            }
        }

        matrixAssembler.setWeight(polyArea);

        TIntArrayList asmIndes = new TIntArrayList(indexValueMap.size());
        double[][] shapeFuncs = new double[SPATIAL_DIMENSION + 1][indexValueMap.size()];
        for (Entry<Integer, double[]> entry : indexValueMap.entrySet()) {
            double[] value = entry.getValue();
            for (int dim = 0; dim < SPATIAL_DIMENSION; dim++) {
                shapeFuncs[dim + 1][asmIndes.size()] = value[dim];
            }
            asmIndes.add(entry.getKey());
        }
        matrixAssembler.setNodesAssemblyIndes(asmIndes);
        matrixAssembler.setTestShapeFunctionValues(shapeFuncs);
        matrixAssembler.setTrialShapeFunctionValues(shapeFuncs);
        matrixAssembler.assemble();
    }

    private void integrateLoad() {

    }

    @Override
    public void setAssemblersGroup(Map<AssemblerType, Assembler> assemblersGroup) {
        super.setAssemblersGroup(assemblersGroup);
        matrixAssembler = assemblersGroup.get(AssemblerType.ASM_VOLUME);
        loadAssembler = assemblersGroup.get(AssemblerType.ASM_VOLUME_LOAD);
    }

    @Override
    public void setIntegralDegree(int integralDegree) {
        super.setIntegralDegree(integralDegree);
        linearQuadratureSupport.setQuadratureDegree(integralDegree);
    }
}
