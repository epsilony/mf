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
package net.epsilony.mf.process.assembler;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class SymmetricT2Value implements T2Value {

    ShapeFunctionValue shapeFunctionValue;

    @Override
    public double getTestValue(int nd, int pd) {
        return shapeFunctionValue.getValue(nd, pd);
    }

    @Override
    public double getTrialValue(int nd, int pd) {
        return shapeFunctionValue.getValue(nd, pd);
    }

    @Override
    public int getNodeAssemblyIndex(int nd) {
        return shapeFunctionValue.getNodeAssemblyIndex(nd);
    }

    @Override
    public int getNodesSize() {
        return shapeFunctionValue.getNodesSize();
    }

    @Override
    public int getMaxPdOrder() {
        return shapeFunctionValue.getMaxPdOrder();
    }

    @Override
    public int getSpatialDimension() {
        return shapeFunctionValue.getSpatialDimension();
    }

    public ShapeFunctionValue getShapeFunctionValue() {
        return shapeFunctionValue;
    }

    public void setShapeFunctionValue(ShapeFunctionValue shapeFunctionValue) {
        this.shapeFunctionValue = shapeFunctionValue;
    }

    public SymmetricT2Value() {
    }

    public SymmetricT2Value(ShapeFunctionValue shapeFunctionValue) {
        this.shapeFunctionValue = shapeFunctionValue;
    }
}