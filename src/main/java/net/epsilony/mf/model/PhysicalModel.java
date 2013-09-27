/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.load.MFLoad;
import java.util.Map;
import net.epsilony.tb.analysis.Dimensional;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface PhysicalModel extends Dimensional {

    GeomUnit getGeomRoot();

    Map<GeomUnit, MFLoad> getLoadMap();
}
