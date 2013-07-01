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

    void setAssembler(Assembler<?> assembler);

    Assembler<?> getAssembler();

    void setMFQuadratureTask(MFQuadratureTask task);

    MFQuadratureTask getMFQuadratureTask();

    void setModel(Model2D model);

    Model2D getModel();

    void setShapeFunction(MFShapeFunction shapeFunction);

    MFShapeFunction getShapeFunction();
}
