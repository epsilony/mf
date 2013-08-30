/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegratePoint extends SimpMFRawIntegratePoint implements MFIntegratePoint {
    double[] load;

    public void setLoad(double[] load) {
        this.load = load;
    }

    @Override
    public double[] getLoad() {
        return load;
    }

    @Override
    public boolean[] getLoadValidity() {
        return null;
    }

    @Override
    public void setDimension(int dim) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
