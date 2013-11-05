/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomUnitSubdomain implements MFSubdomain {

    GeomUnit geomUnit;

    public GeomUnit getGeomUnit() {
        return geomUnit;
    }

    public void setGeomUnit(Segment segment) {
        this.geomUnit = segment;
    }
}
