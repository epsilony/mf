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

import net.epsilony.mf.model.load.DirichletLoadValue;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RawLagrangleAssemblerInput extends RawAssemblerInput<DirichletLoadValue> implements LagrangleAssemblyInput {
    ShapeFunctionValue testLagrangleValue;
    ShapeFunctionValue trailLagrangleValue;

    @Override
    public ShapeFunctionValue getTestLagrangleValue() {
        return testLagrangleValue;
    }

    public void setTestLagrangleValue(ShapeFunctionValue testLagrangleValue) {
        this.testLagrangleValue = testLagrangleValue;
    }

    @Override
    public ShapeFunctionValue getTrialLagrangleValue() {
        return trailLagrangleValue;
    }

    public void setTrailLagrangleValue(ShapeFunctionValue trailLagrangleValue) {
        this.trailLagrangleValue = trailLagrangleValue;
    }

    public void setLagrangleValue(ShapeFunctionValue ttValue) {
        this.testLagrangleValue = ttValue;
        this.trailLagrangleValue = ttValue;
    }

    public RawLagrangleAssemblerInput(double weight, ShapeFunctionValue testValue, ShapeFunctionValue trailValue,
            DirichletLoadValue loadValue, ShapeFunctionValue testLagrangleValue, ShapeFunctionValue trailLagrangleValue) {
        super(weight, testValue, trailValue, loadValue);
        this.testLagrangleValue = testLagrangleValue;
        this.trailLagrangleValue = trailLagrangleValue;
    }

    public RawLagrangleAssemblerInput(double weight, ShapeFunctionValue ttValue, DirichletLoadValue loadValue,
            ShapeFunctionValue ttLagrangleValue) {
        super(weight, ttValue, loadValue);
        this.testLagrangleValue = ttLagrangleValue;
        this.trailLagrangleValue = ttLagrangleValue;
    }

    public RawLagrangleAssemblerInput() {
    }

}
