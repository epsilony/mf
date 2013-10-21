/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFLoad extends Serializable {

    boolean isSynchronizedClonable();

    MFLoad synchronizedClone();
}
