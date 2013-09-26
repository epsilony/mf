/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.model.MFBoundary;
import net.epsilony.tb.analysis.WithDiffOrder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMixer extends WithDiffOrder {

    MixResult mix(double[] center, MFBoundary bnd);
}
