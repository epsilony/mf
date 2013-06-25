/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.tb.analysis.WithDiffOrder;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ShapeFunction extends WithDiffOrder, SynchronizedClonable<ShapeFunction> {

    TDoubleArrayList[] values(
            double[] center,
            List<double[]> nodesCoords,
            TDoubleArrayList nodesInflucenceRads,
            TDoubleArrayList[] nodesDistancesToCenter);
}
