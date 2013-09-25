/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.geomodel;

import net.epsilony.tb.solid.GeomUnit;

public class RawMFBounary extends AbstractMFBoundary {

    GeomUnit geomUnit;

    public void setGeomUnit(GeomUnit geomUnit) {
        this.geomUnit = geomUnit;
    }

    @Override
    public GeomUnit getGeomUnit() {
        return geomUnit;
    }
}
