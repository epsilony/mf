/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.io.Serializable;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.analysis.Dimensional;
import net.epsilony.tb.analysis.WithDiffOrder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFShapeFunction extends IntIdentity, WithDiffOrder, Serializable, Dimensional {

    void setNodes(List<MFNode> nodes);

    void setDistancesToPosition(TDoubleArrayList[] distances);

    void setPosition(double[] position);

    double[][] values(double[][] output);
}
