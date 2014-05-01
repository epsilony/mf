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

package net.epsilony.mf.process.mix;

import java.util.ArrayList;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.support_domain.ArraySupportDomainData;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.shape_func.SimpShapeFunctionValue;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.mf.model.geom.MFGeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Mixer implements MFMixer {

    SupportDomainSearcher supportDomainSearcher;
    MFShapeFunction shapeFunction;

    private final ArraySupportDomainData supportDomainData = new ArraySupportDomainData();
    private final ArrayList<MFNode> visibleNodes = supportDomainData.getVisibleNodesContainer();
    private final SimpShapeFunctionValue result = new SimpShapeFunctionValue();

    public Mixer() {
        result.setAssemblyIndexGetter((index) -> visibleNodes.get(index).getAssemblyIndex());
    }

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
    public void setBoundary(MFGeomUnit boundary) {
        supportDomainSearcher.setBoundary(boundary);
    }

    public void setRadius(double radius) {
        supportDomainSearcher.setRadius(radius);
    }

    @Override
    public ShapeFunctionValue mix() {
        supportDomainSearcher.search(supportDomainData);
        result.setPartialValueTuple(shapeFunction.values());
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
        ArrayList<MFNode> visibleNodesContainer = supportDomainData.getVisibleNodesContainer();
        shapeFunction.setCoordsGetter((index) -> visibleNodesContainer.get(index).getCoord());
        shapeFunction.setInfluenceRadiusGetter((index) -> visibleNodesContainer.get(index).getInfluenceRadius());
        shapeFunction.setInputSizeSupplier(visibleNodesContainer::size);
        this.shapeFunction = shapeFunction;
    }

    @Override
    public String toString() {
        return String.format("%s{influ rad: %f, shape function: %s, support domain searcher: %s}",
                MiscellaneousUtils.simpleToString(this), getShapeFunction(), getSupportDomainSearcher());
    }
}
