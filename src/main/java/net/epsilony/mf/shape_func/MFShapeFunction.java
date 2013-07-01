/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.WithDiffOrder;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFShapeFunction extends WithDiffOrder, SynchronizedClonable<MFShapeFunction> {

    TDoubleArrayList[] values(
            double[] center,
            List<MFNode> nodes,
            TDoubleArrayList[] nodesDistancesToCenter);
}