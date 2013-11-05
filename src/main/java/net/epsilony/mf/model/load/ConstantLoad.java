/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantLoad implements MFLoad {

    double[] value;
    boolean[] validity;

    @Override
    public double[] getValue() {
        return value;
    }

    @Override
    public boolean[] getValidity() {
        return validity;
    }

    @Override
    public boolean isDirichlet() {
        return validity != null;
    }

    public void setValue(double[] value) {
        this.value = value;
    }

    public void setValidity(boolean[] validity) {
        this.validity = validity;
    }
}
