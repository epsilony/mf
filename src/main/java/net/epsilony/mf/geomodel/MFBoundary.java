/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFBoundary extends IntIdentity {

    MFBoundary getPast();

    void setPast(MFBoundary past);

    GeomUnit getGeomUnit();

    void setGeomUnit(GeomUnit geomUnit);

    MFLoad getLoad();

    void setLoad(MFLoad load);
}
