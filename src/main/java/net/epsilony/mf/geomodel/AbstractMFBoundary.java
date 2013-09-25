/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFBoundary implements MFBoundary {

    MFBoundary past;
    int id;
    MFLoad load;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public MFBoundary getPast() {
        return past;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setPast(MFBoundary Past) {
        this.past = Past;
    }

    @Override
    public MFLoad getLoad() {
        return load;
    }

    @Override
    public void setLoad(MFLoad load) {
        this.load = load;
    }
}
