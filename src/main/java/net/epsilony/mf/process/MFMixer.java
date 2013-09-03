/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.tb.analysis.WithDiffOrder;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMixer extends WithDiffOrder {

    MixResult mix(double[] center, Segment bnd);
    
}