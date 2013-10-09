/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.subdomain.QuadrangleSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.VolumeLoad;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.RawMFIntegratePoint;
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
        if (quadratueDomain instanceof QuadrangleSubdomain) {
            return produceByQuadrangle((QuadrangleSubdomain) quadratueDomain);
        }
        throw new IllegalStateException();
    }

    private List<MFIntegratePoint> produceByQuadrangle(QuadrangleSubdomain quadrangleSubdomain) {
        quadrangleQuadrature.setQuadrangle(
                quadrangleSubdomain.getVertex(0)[0], quadrangleSubdomain.getVertex(0)[1],
                quadrangleSubdomain.getVertex(1)[0], quadrangleSubdomain.getVertex(1)[1],
                quadrangleSubdomain.getVertex(2)[0], quadrangleSubdomain.getVertex(2)[1],
                quadrangleSubdomain.getVertex(3)[0], quadrangleSubdomain.getVertex(3)[1]);
        int pointsNumPerDim = GaussLegendre.pointsNum(quadratureDegree);
        ArrayList<MFIntegratePoint> result = new ArrayList<>(pointsNumPerDim * pointsNumPerDim);
        Iterator<QuadraturePoint> iter = quadrangleQuadrature.iterator();
        VolumeLoad vmLoad = (VolumeLoad) volumeLoad;
        while (iter.hasNext()) {
            QuadraturePoint qp = iter.next();
            RawMFIntegratePoint pt = new RawMFIntegratePoint();
            pt.setCoord(qp.coord);
            pt.setWeight(qp.weight);
            if (null != vmLoad) {
                pt.setLoad(vmLoad.getLoad(qp.coord));
            }
            result.add(pt);
        }
        return result;
    }
}
