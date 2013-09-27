/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.load;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface VolumeLoad extends MFLoad {

    double[] getLoad(double[] coord);
}
