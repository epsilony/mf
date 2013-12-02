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
package net.epsilony.mf.process.integrate.core.oned;

import gnu.trove.list.array.TIntArrayList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.tool.LinearQuadratureSupport;
import net.epsilony.mf.process.integrate.unit.GeomUnitSubdomain;
import net.epsilony.mf.util.DoubleHolder;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class StabilizedConformingLineIntegratorCore extends AbstractMFIntegratorCore {
    protected final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();
    private Assembler loadAssembler;
    private Assembler matrixAssembler;
    private Line line;
    private final LinkedHashMap<Integer, DoubleHolder> indexValueMap = new LinkedHashMap<>();

    public StabilizedConformingLineIntegratorCore() {
        super();
        processType = MFProcessType.VOLUME;
    }

    @Override
    public void integrate() {
        GeomUnitSubdomain geomUnitSubdomain = (GeomUnitSubdomain) integrateUnit;
        line = (Line) geomUnitSubdomain.getGeomUnit();
        integrateMatrix();
        integrateLoad();

    }

    private void integrateMatrix() {
        double length = line.length();
        mixer.setDiffOrder(0);
        mixer.setBoundary(null);

        double[][] coords = new double[][] { line.getStartCoord(), line.getEndCoord() };
        double sign = Math.signum(line.getStartCoord()[0] - line.getEndCoord()[0]);
        double[] norms = new double[] { sign, -sign };
        indexValueMap.clear();
        for (int i = 0; i < coords.length; i++) {
            double[] coord = coords[i];
            double norm = norms[i];
            mixer.setCenter(coord);
            MixResult mixResult = mixer.mix();
            TIntArrayList nodesAssemblyIndes = mixResult.getNodesAssemblyIndes();
            double[][] shapeFunctionValues = mixResult.getShapeFunctionValues();
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                Integer asmIndex = nodesAssemblyIndes.getQuick(j);
                DoubleHolder valueHolder = indexValueMap.get(asmIndex);
                if (null == valueHolder) {
                    valueHolder = new DoubleHolder();
                    indexValueMap.put(asmIndex, valueHolder);
                }
                valueHolder.value += shapeFunctionValues[0][j] * norm / length;
            }
        }

        matrixAssembler.setWeight(length);

        TIntArrayList asmIndes = new TIntArrayList(indexValueMap.size());
        double[][] shapeFuncs = new double[2][indexValueMap.size()];
        for (Entry<Integer, DoubleHolder> entry : indexValueMap.entrySet()) {
            shapeFuncs[1][asmIndes.size()] = entry.getValue().value;
            asmIndes.add(entry.getKey());
        }
        matrixAssembler.setNodesAssemblyIndes(asmIndes);
        matrixAssembler.setTestShapeFunctionValues(shapeFuncs);
        matrixAssembler.setTrialShapeFunctionValues(shapeFuncs);
        matrixAssembler.assemble();
    }

    private void integrateLoad() {

        LockableHolder<MFLoad> lockableHolder = OneDIntegratorCoreUtils.searchLineLoad(line, loadMap);
        if (null == lockableHolder) {
            return;
        }
        linearQuadratureSupport.setStartEndCoords(line.getStartCoord(), line.getEndCoord());
        linearQuadratureSupport.reset();
        double[] loadValue = null;
        while (linearQuadratureSupport.hasNext()) {
            linearQuadratureSupport.next();
            ReentrantLock lock = lockableHolder.getLock();
            try {
                lock.lock();
                SegmentLoad load = (SegmentLoad) lockableHolder.getData();
                load.setSegment(line);
                load.setParameter(linearQuadratureSupport.getLinearParameter());
                loadValue = load.getValue();
            } finally {
                lock.unlock();
            }
            if (null == loadValue) {
                continue;
            }

            mixer.setDiffOrder(0);
            mixer.setCenter(linearQuadratureSupport.getLinearCoord());
            mixer.setBoundary(null);
            MixResult mixResult = mixer.mix();
            loadAssembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            loadAssembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            loadAssembler.setLoad(loadValue, null);

            loadAssembler.setWeight(linearQuadratureSupport.getLinearWeight());

            loadAssembler.assemble();
        }
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
