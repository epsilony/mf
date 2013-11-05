/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantLoad implements MFLoad {

    double[] load;
    boolean[] loadValidity;

    public double[] getLoad() {
        return load;
    }

    public boolean[] getLoadValidity() {
        return loadValidity;
    }

    public boolean isDirichlet() {
        return loadValidity != null;
    }

    public void setLoad(double[] load) {
        this.load = load;
    }

    public void setLoadValidity(boolean[] loadValidity) {
        this.loadValidity = loadValidity;
    }
}
