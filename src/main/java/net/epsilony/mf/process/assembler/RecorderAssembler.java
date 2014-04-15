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

import java.util.ArrayList;
import java.util.List;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RecorderAssembler implements LagrangleAssembler {

    int lagrangleNodesNum;
    int spatialDimension;
    int valueDimension;
    int allNodesNum;
    List<AssemblyInput> inputRecords = new ArrayList<>();
    AssemblyInput assemblyInput;
    MFMatrix mainVector, mainMatrix;

    @Override
    public void assemble() {
        inputRecords.add(assemblyInput);
    }

    public AssemblyInput getAssemblyInput() {
        return assemblyInput;
    }

    @Override
    public void setAssemblyInput(AssemblyInput assemblyInput) {
        this.assemblyInput = assemblyInput;
    }

    public int getLagrangleNodesNum() {
        return lagrangleNodesNum;
    }

    @Override
    public void setLagrangleNodesNum(int lagrangleNodesNum) {
        this.lagrangleNodesNum = lagrangleNodesNum;
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    @Override
    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    public int getValueDimension() {
        return valueDimension;
    }

    @Override
    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }

    public int getAllNodesNum() {
        return allNodesNum;
    }

    @Override
    public void setAllNodesNum(int allNodesNum) {
        this.allNodesNum = allNodesNum;
    }

    public List<AssemblyInput> getInputRecords() {
        return inputRecords;
    }

    public void setInputRecords(List<AssemblyInput> inputRecords) {
        this.inputRecords = inputRecords;
    }

    public int getRequiredMatrixSize() {
        return 0;
    }

    public MFMatrix getMainVector() {
        return mainVector;
    }

    @Override
    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    public MFMatrix getMainMatrix() {
        return mainMatrix;
    }

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

}
