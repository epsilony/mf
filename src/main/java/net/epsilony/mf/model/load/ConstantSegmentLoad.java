/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantSegmentLoad implements SegmentLoad {

    double[] load;
    boolean[] loadValidity;

    @Override
    public void setSegment(Segment seg) {
    }

    @Override
    public void setParameter(double parm) {
    }

    @Override
    public double[] getLoad() {
        return load;
    }

    @Override
    public boolean[] getLoadValidity() {
        return loadValidity;
    }

    public void setLoad(double[] load) {
        this.load = load;
    }

    public void setLoadValidity(boolean[] loadValidity) {
        this.loadValidity = loadValidity;
    }

    @Override
    public boolean isDirichlet() {
        return loadValidity != null;
    }
}
