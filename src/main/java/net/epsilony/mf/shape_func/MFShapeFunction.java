/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.analysis.WithDiffOrder;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFShapeFunction extends WithDiffOrder, SynchronizedClonable<MFShapeFunction> {

    void setNodes(List<MFNode> nodes);
    
    void setDistancesToPosition(TDoubleArrayList[] distances);
    
    void setPosition(double[] position);
    
    double[][] values(double[][] output);
}
