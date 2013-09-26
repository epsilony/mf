/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.List;
import net.epsilony.tb.analysis.Dimensional;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface GeomModel extends Dimensional {

    List<? extends MFBoundary> getBoundaries();
}