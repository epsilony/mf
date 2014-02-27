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

package net.epsilony.mf.process;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Iterator;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.support_domain.SoftSupportDomainData;
import net.epsilony.mf.model.support_domain.SupportDomainData;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.process.assembler.SettableShapeFunctionValue;
import net.epsilony.mf.process.assembler.ShapeFunctionValue;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.util.MFConstants;
import net.epsilony.tb.Factory;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.solid.GeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Mixer implements MFMixer {

    public static final int DEFAULT_CACHE_CAPACITY = 60;
    ArrayList<double[]> coords = new ArrayList<>(DEFAULT_CACHE_CAPACITY);
    TDoubleArrayList infRads = new TDoubleArrayList(DEFAULT_CACHE_CAPACITY);
    SupportDomainSearcher supportDomainSearcher;
    MFShapeFunction shapeFunction;

    Factory<? extends SettableShapeFunctionValue> settableShapeFunctionValueFactory;
    SupportDomainData supportDomainData = new SoftSupportDomainData();

    @Override
    public void setUnitOutNormal(double[] unitOutNormal) {
        supportDomainSearcher.setUnitOutNormal(unitOutNormal);
    }

    @Override
    public void setCenter(double[] center) {
        supportDomainSearcher.setCenter(center);
        shapeFunction.setPosition(center);
    }

    @Override
    public void setBoundary(GeomUnit boundary) {
        supportDomainSearcher.setBoundary(boundary);
    }

    @Override
    public ShapeFunctionValue mix() {
        supportDomainSearcher.search(supportDomainData);
        if (MFConstants.SUPPORT_COMPLEX_CRITERION) {
            throw new UnsupportedOperationException();
        }

        shapeFunction.setNodes(supportDomainData.getVisibleNodesContainer());

        double[][] values = shapeFunction.values(null);
        SettableShapeFunctionValue result = settableShapeFunctionValueFactory.produce();
        result.resize(supportDomainData.getVisibleNodesContainer().size(), getDiffOrder(), getShapeFunction()
                .getDimension());
        Iterator<MFNode> nodesIterator = supportDomainData.getVisibleNodesContainer().iterator();
        for (int i = 0; nodesIterator.hasNext(); i++) {
            MFNode node = nodesIterator.next();
            result.setNodeAssemblyIndex(i, node.getAssemblyIndex());
            for (int dim = 0; dim < values.length; dim++) {
                result.setValue(i, dim, values[dim][i]);
            }
        }
        return result;
    }

    @Override
    public int getDiffOrder() {
        return shapeFunction.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        shapeFunction.setDiffOrder(diffOrder);
    }

    public SupportDomainSearcher getSupportDomainSearcher() {
        return supportDomainSearcher;
    }

    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this.supportDomainSearcher = supportDomainSearcher;
    }

    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
        shapeFunction.setDiffOrder(0);
    }

    @Override
    public String toString() {
        return String.format("%s{influ rad: %f, shape function: %s, support domain searcher: %s}",
                MiscellaneousUtils.simpleToString(this), getShapeFunction(), getSupportDomainSearcher());
    }
}
