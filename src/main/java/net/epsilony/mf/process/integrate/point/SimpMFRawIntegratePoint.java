/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFRawIntegratePoint implements MFRawIntegratePoint {


    int id;
    double[] coord;
    double weight;
    int dimension;

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
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
