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
package net.epsilony.mf.integrate.integrator.vc;

import java.util.function.Consumer;
import java.util.function.IntFunction;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.SimpGeomPoint;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.math.PartialVectorFunction;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class VCIntegrator2D {
    private MFMixer mixer;

    private IntFunction<PartialVectorFunction> assemblyIndexToBasesFunction;

    private IntFunction<VCIntegralNode> assemblyIndexToVCNodeGetter;

    private Consumer<IntegralMixRecordEntry> volumeAssociatedRecorder;

    private Consumer<IntegralMixRecordEntry> neumannAssociatedRecorder;

    private Consumer<IntegralMixRecordEntry> dirichletAssociatedRecorder;

    public void volumeIntegrate(GeomQuadraturePoint gqp) {
        GeomPoint geomPoint = gqp.getGeomPoint();

        double[] coord = geomPoint.getCoord();
        mixer.setCenter(coord);
        mixer.setBoundary(null);
        mixer.setUnitOutNormal(null);
        mixer.setDiffOrder(1);

        double weight = gqp.getWeight();
        ShapeFunctionValue mix = mixer.mix();
        for (int i = 0; i < mix.size(); i++) {
            PartialValue nodeValue = mix.sub(i);
            int nodeAssemblyIndex = mix.getNodeAssemblyIndex(i);
            VCIntegralNode vcNode = assemblyIndexToVCNodeGetter.apply(nodeAssemblyIndex);
            PartialVectorFunction basesFunction = assemblyIndexToBasesFunction.apply(nodeAssemblyIndex);
            basesFunction.setMaxPartialOrder(1);
            vcNode.volumeIntegrate(nodeValue, basesFunction.value(coord), weight);
        }

        if (null != volumeAssociatedRecorder) {
            volumeAssociatedRecorder.accept(copyToRecord(gqp, mix));
        }
    }

    private IntegralMixRecordEntry copyToRecord(GeomQuadraturePoint gqp, ShapeFunctionValue mix) {
        SimpIntegralMixRecordEntry result = new SimpIntegralMixRecordEntry();
        result.setWeight(gqp.getWeight());
        result.setGeomPoint(new SimpGeomPoint(gqp.getGeomPoint()));
        result.setShapeFunctionValue(mix.copy());
        return result;
    }

    public void neumannIntegrate(GeomQuadraturePoint gqp) {
        GeomPoint geomPoint = gqp.getGeomPoint();
        int maxPartialOrder = 0;
        ShapeFunctionValue mix = mix(geomPoint, maxPartialOrder);

        boundaryIntegrate(gqp, mix);

        if (null != neumannAssociatedRecorder) {
            neumannAssociatedRecorder.accept(copyToRecord(gqp, mix));
        }
    }

    public void dirichletIntegrate(GeomQuadraturePoint gqp) {
        GeomPoint geomPoint = gqp.getGeomPoint();
        int maxPartialOrder = 0;
        ShapeFunctionValue mix = mix(geomPoint, maxPartialOrder);

        boundaryIntegrate(gqp, mix);
        if (null != dirichletAssociatedRecorder) {
            dirichletAssociatedRecorder.accept(copyToRecord(gqp, mix));
        }
    }

    private ShapeFunctionValue mix(GeomPoint geomPoint, int maxPartialOrder) {
        double[] coord = geomPoint.getCoord();
        GeomUnit geomUnit = geomPoint.getGeomUnit();
        mixer.setCenter(coord);
        mixer.setBoundary(geomUnit);
        mixer.setUnitOutNormal(null);
        mixer.setDiffOrder(maxPartialOrder);
        ShapeFunctionValue mix = mixer.mix();
        return mix;
    }

    private void boundaryIntegrate(GeomQuadraturePoint gqp, ShapeFunctionValue mix) {
        GeomPoint geomPoint = gqp.getGeomPoint();
        double[] coord = geomPoint.getCoord();
        GeomUnit geomUnit = geomPoint.getGeomUnit();
        Segment segment = (Segment) geomUnit;
        double[] unitOutNormal = new double[2];
        Segment2DUtils.chordUnitOutNormal(segment, unitOutNormal);

        double weight = gqp.getWeight();

        for (int i = 0; i < mix.size(); i++) {
            PartialValue nodeValue = mix.sub(i);
            int nodeAssemblyIndex = mix.getNodeAssemblyIndex(i);
            VCIntegralNode vcNode = assemblyIndexToVCNodeGetter.apply(nodeAssemblyIndex);
            PartialVectorFunction basesFunction = assemblyIndexToBasesFunction.apply(nodeAssemblyIndex);
            basesFunction.setMaxPartialOrder(0);
            vcNode.boundaryIntegrate(nodeValue, basesFunction.value(coord), weight, unitOutNormal);
        }
    }

    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }

    public void setAssemblyIndexToVCNodeGetter(IntFunction<VCIntegralNode> assemblyIndexToVCNodeGetter) {
        this.assemblyIndexToVCNodeGetter = assemblyIndexToVCNodeGetter;
    }

    public void setVolumeAssociatedRecorder(Consumer<IntegralMixRecordEntry> volumeAssociatedRecorder) {
        this.volumeAssociatedRecorder = volumeAssociatedRecorder;
    }

    public void setNeumannAssociatedRecorder(Consumer<IntegralMixRecordEntry> neumannAssociatedRecorder) {
        this.neumannAssociatedRecorder = neumannAssociatedRecorder;
    }

    public void setDirichletAssociatedRecorder(Consumer<IntegralMixRecordEntry> dirichletAssociatedRecorder) {
        this.dirichletAssociatedRecorder = dirichletAssociatedRecorder;
    }

    public void setAssemblyIndexToBasesFunction(IntFunction<PartialVectorFunction> assemblyIndexToBasesFunction) {
        this.assemblyIndexToBasesFunction = assemblyIndexToBasesFunction;
    }

}
