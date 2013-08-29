/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.tb.analysis.Dimensional;

/**
 *
 * @author <a href="mailto:epsionyuan@gmail.com">Man YUAN</a>
 */
public interface MFLoad extends Dimensional {

    double[] getLoad();

    boolean[] getLoadValidity();
}
