/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TIntArrayList;
import java.util.List;
import net.epsilony.mf.model.MFNode;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMixResult implements MixResult {

    List<MFNode> nodes;
    double[][] shapeFunctionValues;
    TIntArrayList nodesAssemblyIndes;

    @Override
    public List<MFNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<MFNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public double[][] getShapeFunctionValues() {
        return shapeFunctionValues;
    }

    public void setShapeFunctionValues(double[][] shapeFunctionValues) {
        this.shapeFunctionValues = shapeFunctionValues;
    }

    @Override
    public TIntArrayList getNodesAssemblyIndes() {
        return nodesAssemblyIndes;
    }

    public void setNodesAssemblyIndes(TIntArrayList nodesAssemblyIndes) {
        this.nodesAssemblyIndes = nodesAssemblyIndes;
    }

}
