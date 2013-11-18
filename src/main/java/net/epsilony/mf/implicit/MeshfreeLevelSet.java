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

package net.epsilony.mf.implicit;


/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MeshfreeLevelSet {

    // LevelSetApproximationAssembler assembler = new
    // LevelSetApproximationAssembler();
    // MFShapeFunction shapeFunction = new MLS();
    // SimpMFProject mfProject = new SimpMFProject();
    // protected MFLinearMechanicalProcessor processor;
    //
    // public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator
    // influenceRadiusCalculator) {
    // mfProject.put(INFLUENCE_RADIUS_CALCULATOR, influenceRadiusCalculator);
    // }
    //
    // public void setWeightFunction(RadialBasisCore weightFunction) {
    // assembler.setWeightFunction(weightFunction);
    // }
    //
    // public void setMFQuadratureTask(Map<MFProcessType, MFIntegrateUnit>
    // mfQuadratureTask) {
    // mfProject.put(INTEGRATE_UNITS_GROUP, mfQuadratureTask);
    // }
    //
    // public void setModel(AnalysisModel model) {
    // mfProject.put(ANALYSIS_MODEL, model);
    // }
    //
    // public void setShapeFunction(MFShapeFunction shapeFunction) {
    // this.shapeFunction = shapeFunction;
    // }
    //
    // public void prepare() {
    //
    // // mfProject.setAssembler(assembler);
    // mfProject.put(SHAPE_FUNCTION, shapeFunction);
    // processor = new MFLinearMechanicalProcessor();
    // processor.setProject(mfProject);
    // processor.preprocess();
    // processor.solve();
    // }
    //
    // public DifferentiableFunction getLevelSetFunction() {
    // final PostProcessor postProcessor = processor.genPostProcessor();
    // return new DifferentiableFunction() {
    // @Override
    // public double[] value(double[] input, double[] output) {
    // double[] result = postProcessor.value(input, null);
    // if (null == output) {
    // return result;
    // } else {
    // System.arraycopy(result, 0, output, 0, result.length);
    // return output;
    // }
    // }
    //
    // @Override
    // public int getInputDimension() {
    // return 2;
    // }
    //
    // @Override
    // public int getOutputDimension() {
    // return 1;
    // }
    //
    // @Override
    // public int getDiffOrder() {
    // return postProcessor.getDiffOrder();
    // }
    //
    // @Override
    // public void setDiffOrder(int diffOrder) {
    // postProcessor.setDiffOrder(diffOrder);
    // }
    // };
    // }
}
