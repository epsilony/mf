/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TIntArrayList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MixResult {

    List<MFNode> getNodes();

    double[][] getShapeFunctionValues();

    TIntArrayList getNodesAssemblyIndes();
}
