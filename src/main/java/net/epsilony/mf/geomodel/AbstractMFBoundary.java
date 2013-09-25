/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFBoundary implements MFBoundary {

    MFBoundary past;
    int id;

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

    public void setPast(MFBoundary Past) {
        this.past = Past;
    }
}
