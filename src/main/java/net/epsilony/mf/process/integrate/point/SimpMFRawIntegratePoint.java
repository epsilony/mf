/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFRawIntegratePoint implements MFRawIntegratePoint{
    double[] coord;
    double weight;

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
}
