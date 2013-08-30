/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegratePoint implements MFIntegratePoint {

    double weight;
    double[] coord;
    double[] load;

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setCoord(double[] coord) {
        this.coord = coord;
    }

    public void setLoad(double[] load) {
        this.load = load;
    }

    @Override
    public double[] getCoord() {
        return coord;
    }

    @Override
    public double getWeight() {
        return weight;
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
