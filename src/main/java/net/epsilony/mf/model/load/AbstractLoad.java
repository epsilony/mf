/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractLoad implements MFLoad {

    @Override
    public boolean[] getValidity() {
        return null;
    }

    @Override
    public boolean isDirichlet() {
        return false;
    }

}
