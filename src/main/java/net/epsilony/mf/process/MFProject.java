/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProject {

    PostProcessor genPostProcessor();

    MFProcessor genProcessor();

    Assembler getAssembler();

    MFQuadratureTask getMFQuadratureTask();

    Model2D getModel();

    MFShapeFunction getShapeFunction();
}
