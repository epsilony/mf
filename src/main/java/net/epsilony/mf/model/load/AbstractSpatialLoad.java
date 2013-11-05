/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractSpatialLoad extends AbstractLoad implements SpatialLoad {

    protected double[] coord;

    @Override
    public void setCoord(double[] coord) {
        this.coord = coord;
    }

}
