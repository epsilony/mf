/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.SimpMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.Polygon2DModel;
import net.epsilony.mf.geomodel.search.SegmentsMidPointLRTreeRangeSearcher;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractModel2DTask {

    private int id;
    protected List<BCSpecification> dirichletBCs = new LinkedList<>();
    protected Polygon2DModel model;
    protected List<BCSpecification> neumannBCs = new LinkedList<>();
    protected SegmentsMidPointLRTreeRangeSearcher polygonSegmentsRangeSearcher;
    protected int segQuadDegree;
    protected GenericFunction<double[], double[]> volumeForceFunc;

    public void addDirichletBoundaryCondition(double[] from, double[] to, GenericFunction<double[], double[]> valueFunc, GenericFunction<double[], boolean[]> markFunc) {
        addDirichletBoundaryCondition(new BCSpecification(from, to, valueFunc, markFunc));
    }

    public void addDirichletBoundaryCondition(BCSpecification spec) {
        dirichletBCs.add(spec);
    }

    public void addNeumannBoundaryCondition(BCSpecification spec) {
        neumannBCs.add(spec);
    }

    public void addNeumannBoundaryCondition(double[] from, double[] to, GenericFunction<double[], double[]> valueFunc) {
        addNeumannBoundaryCondition(new BCSpecification(from, to, valueFunc, null));
    }

    public List<MFBoundaryIntegratePoint> dirichletTasks() {
        LinkedList<MFBoundaryIntegratePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature();
        segQuad.setDegree(segQuadDegree);
        for (BCSpecification spec : dirichletBCs) {
            List<Segment> segs = polygonSegmentsRangeSearcher.rangeSearch(spec.from, spec.to);
            for (Segment seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                GenericFunction<double[], boolean[]> markFunc = spec.markFunc;
                segQuad.setSegment(seg);
                for (Segment2DQuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    boolean[] mark = markFunc.value(qp.coord, null);
                    res.add(new SimpMFBoundaryIntegratePoint(qp, value, mark));
                }
            }
        }
        return res;
    }

    public Polygon2DModel getModel() {
        return model;
    }

    public List<MFBoundaryIntegratePoint> neumannTasks() {
        LinkedList<MFBoundaryIntegratePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature();
        segQuad.setDegree(segQuadDegree);
        for (BCSpecification spec : neumannBCs) {
            List<Segment> segs = polygonSegmentsRangeSearcher.rangeSearch(spec.from, spec.to);
            for (Segment seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                segQuad.setSegment(seg);
                for (Segment2DQuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    res.add(new SimpMFBoundaryIntegratePoint(qp, value, null));
                }
            }
        }
        return res;
    }

    public void setModel(Polygon2DModel model) {
        this.model = model;
        polygonSegmentsRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(model.getPolygon());
    }

    public void setSegmentQuadratureDegree(int segQuadDegree) {
        this.segQuadDegree = segQuadDegree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class BCSpecification {

        public double[] from, to;
        public GenericFunction<double[], double[]> valueFunc;
        public GenericFunction<double[], boolean[]> markFunc;

        public BCSpecification() {
        }

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
