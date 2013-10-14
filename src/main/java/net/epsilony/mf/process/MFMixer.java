/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.tb.analysis.WithDiffOrder;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMixer extends WithDiffOrder {

    void setBoundary(GeomUnit boundary);

    void setCenter(double[] center);

    void setUnitOutNormal(double[] unitNormal);

    MixResult mix();
}
