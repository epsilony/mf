/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.unit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMFIntegratePoint implements MFIntegratePoint {

    int id;
    double[] coord;
    double weight;
    double[] load;
    boolean[] loadValidity;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public double[] getCoord() {
        return coord;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setCoord(double[] coord) {
        this.coord = coord;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public double[] getLoad() {
        return load;
    }

    public void setLoad(double[] load) {
        this.load = load;
    }

    @Override
    public boolean[] getLoadValidity() {
        return loadValidity;
    }

    public void setLoadValidity(boolean[] loadValidity) {
        this.loadValidity = loadValidity;
    }
}
