/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SpatialLoad;
import net.epsilony.mf.model.subdomain.PolygonSubdomain;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.quadrature.GaussLegendre;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class NormalVolumeIntegratePointsFactory implements Factory<List<MFIntegratePoint>> {

    MFLoad volumeLoad;
    QuadrangleQuadrature quadrangleQuadrature = new QuadrangleQuadrature();
    int quadratureDegree = -1;
    MFSubdomain quadratueDomain;

    public MFLoad getVolumeLoad() {
        return volumeLoad;
    }

    public void setVolumeLoad(MFLoad volumeLoad) {
        this.volumeLoad = volumeLoad;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        if (this.quadratureDegree == quadratureDegree) {
            return;
        }
        this.quadratureDegree = quadratureDegree;
        quadrangleQuadrature.setDegree(quadratureDegree);
    }

    public MFSubdomain getQuadratueDomain() {
        return quadratueDomain;
    }

    public void setQuadratueDomain(MFSubdomain quadratueDomain) {
        this.quadratueDomain = quadratueDomain;
    }

    @Override
    public List<MFIntegratePoint> produce() {
        if (quadratueDomain instanceof PolygonSubdomain) {
            PolygonSubdomain polydomain = (PolygonSubdomain) quadratueDomain;
            if (polydomain.getVertesSize() == 4) {
                return produceByQuadrangle(polydomain);
            }
        }
        throw new IllegalStateException();
    }

    private List<MFIntegratePoint> produceByQuadrangle(PolygonSubdomain quadrangleSubdomain) {
        quadrangleQuadrature.setQuadrangle(quadrangleSubdomain.getVertexCoord(0)[0],
                quadrangleSubdomain.getVertexCoord(0)[1], quadrangleSubdomain.getVertexCoord(1)[0],
                quadrangleSubdomain.getVertexCoord(1)[1], quadrangleSubdomain.getVertexCoord(2)[0],
                quadrangleSubdomain.getVertexCoord(2)[1], quadrangleSubdomain.getVertexCoord(3)[0],
                quadrangleSubdomain.getVertexCoord(3)[1]);
        int pointsNumPerDim = GaussLegendre.pointsNum(quadratureDegree);
        ArrayList<MFIntegratePoint> result = new ArrayList<>(pointsNumPerDim * pointsNumPerDim);
        Iterator<QuadraturePoint> iter = quadrangleQuadrature.iterator();
        SpatialLoad vmLoad = (SpatialLoad) volumeLoad;
        while (iter.hasNext()) {
            QuadraturePoint qp = iter.next();
            RawMFIntegratePoint pt = new RawMFIntegratePoint();
            pt.setCoord(qp.coord);
            pt.setWeight(qp.weight);
            if (null != vmLoad) {
                vmLoad.setCoord(qp.coord);
                pt.setLoad(vmLoad.getValue());
            }
            result.add(pt);
        }
        return result;
    }
}
