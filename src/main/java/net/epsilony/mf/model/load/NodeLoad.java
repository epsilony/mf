/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

import net.epsilony.mf.model.MFNode;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class NodeLoad implements MFLoad {

    double[] load;
    boolean[] loadValidity;

    public boolean isDirichlet() {
        return loadValidity != null;
    }

    public double[] getLoad() {
        return load;
    }

    public void setLoad(double[] load) {
        this.load = load;
    }

    public boolean[] getLoadValidity() {
        return loadValidity;
    }

    public void setLoadValidity(boolean[] loadValidity) {
        this.loadValidity = loadValidity;
    }
}
