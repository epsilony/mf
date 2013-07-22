/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.search.SegmentsMidPointLRTreeRangeSearcher;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadrature;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Model2DTask implements MFQuadratureTask {

    GeomModel2D model;
    SegmentsMidPointLRTreeRangeSearcher polygonSegmentsRangeSearcher;
    List<BCSpecification> neumannBCs = new LinkedList<>();
    List<BCSpecification> dirichletBCs = new LinkedList<>();
    Collection<? extends QuadraturePoint> volumeQuadraturePoints;
    GenericFunction<double[], double[]> volumeForceFunc;
    int segQuadDegree;

    public void setModel(GeomModel2D model) {
        this.model = model;
        polygonSegmentsRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(model.getPolygon());
    }

    public void setSegmentQuadratureDegree(int segQuadDegree) {
        this.segQuadDegree = segQuadDegree;
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends QuadraturePoint> quadraturePoints) {
        this.volumeForceFunc = volumnForceFunc;
        volumeQuadraturePoints = quadraturePoints;
    }

    @Override
    public List<MFQuadraturePoint> volumeTasks() {
        LinkedList<MFQuadraturePoint> res = new LinkedList<>();
        for (QuadraturePoint qp : volumeQuadraturePoints) {
            double[] volForce = volumeForceFunc == null ? null : volumeForceFunc.value(qp.coord, null);
            res.add(new MFQuadraturePoint(qp, volForce, null));
        }
        return res;
    }

    @Override
    public List<MFQuadraturePoint> neumannTasks() {
        LinkedList<MFQuadraturePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature();
        segQuad.setDegree(segQuadDegree);
        for (BCSpecification spec : neumannBCs) {
            List<Segment> segs = polygonSegmentsRangeSearcher.rangeSearch(spec.from, spec.to);
            for (Segment seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                segQuad.setSegment(seg);
                for (QuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    res.add(new MFQuadraturePoint(qp, value, null));
                }
            }
        }
        return res;
    }

    @Override
    public List<MFQuadraturePoint> dirichletTasks() {
        LinkedList<MFQuadraturePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature();
        segQuad.setDegree(segQuadDegree);
        for (BCSpecification spec : dirichletBCs) {
            List<Segment> segs = polygonSegmentsRangeSearcher.rangeSearch(spec.from, spec.to);
            for (Segment seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                GenericFunction<double[], boolean[]> markFunc = spec.markFunc;
                segQuad.setSegment(seg);
                for (QuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    boolean[] mark = markFunc.value(qp.coord, null);
                    res.add(new MFQuadraturePoint(qp, value, mark));
                }
            }
        }
        return res;
    }

    public void addNeumannBoundaryCondition(BCSpecification spec) {
        neumannBCs.add(spec);
    }

    public void addNeumannBoundaryCondition(
            double[] from, double[] to,
            GenericFunction<double[], double[]> valueFunc) {
        addNeumannBoundaryCondition(new BCSpecification(from, to, valueFunc, null));
    }

    public void addDirichletBoundaryCondition(
            double[] from, double[] to,
            GenericFunction<double[], double[]> valueFunc,
            GenericFunction<double[], boolean[]> markFunc) {
        addDirichletBoundaryCondition(new BCSpecification(from, to, valueFunc, markFunc));
    }

    public void addDirichletBoundaryCondition(BCSpecification spec) {
        dirichletBCs.add(spec);
    }

    public static class BCSpecification {

        double[] from, to;
        GenericFunction<double[], double[]> valueFunc;
        GenericFunction<double[], boolean[]> markFunc;

        public BCSpecification(
                double[] from, double[] to,
                GenericFunction<double[], double[]> valueFunc,
                GenericFunction<double[], boolean[]> markFunc) {
            this.from = from;
            this.to = to;
            this.valueFunc = valueFunc;
            this.markFunc = markFunc;
        }
    }
}
