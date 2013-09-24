/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.SimpMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.GeomModel;
import net.epsilony.mf.geomodel.MFLineBnd;
import net.epsilony.mf.geomodel.search.GenericSegmentLRTreeSearcher;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractModel2DTask {

    private int id;
    protected List<BCSpecification> dirichletBCs = new LinkedList<>();
    protected GeomModel model;
    protected List<BCSpecification> neumannBCs = new LinkedList<>();
    protected GenericSegmentLRTreeSearcher<MFLineBnd> bndSearcher;
    protected int quadratureDegree;
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
        segQuad.setDegree(quadratureDegree);
        for (BCSpecification spec : dirichletBCs) {
            List<MFLineBnd> bnds = bndSearcher.rangeSearch(spec.from, spec.to);
            for (MFLineBnd lineBnd : bnds) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                GenericFunction<double[], boolean[]> markFunc = spec.markFunc;
                segQuad.setSegment(lineBnd.getLine());
                for (Segment2DQuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    boolean[] mark = markFunc.value(qp.coord, null);
                    SimpMFBoundaryIntegratePoint pt = new SimpMFBoundaryIntegratePoint();
                    pt.setCoord(qp.coord);
                    pt.setWeight(qp.weight);
                    pt.setLoad(value);
                    pt.setLoadValidity(mark);
                    pt.setBoundary(lineBnd);
                    pt.setBoundaryParameter(qp.segmentParameter);
                    res.add(pt);
                }
            }
        }
        return res;
    }

    public GeomModel getModel() {
        return model;
    }

    public List<MFBoundaryIntegratePoint> neumannTasks() {
        LinkedList<MFBoundaryIntegratePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature();
        segQuad.setDegree(quadratureDegree);
        for (BCSpecification spec : neumannBCs) {
            List<MFLineBnd> lineBnds = bndSearcher.rangeSearch(spec.from, spec.to);
            for (MFLineBnd lineBnd : lineBnds) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                segQuad.setSegment(lineBnd.getLine());
                for (Segment2DQuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    SimpMFBoundaryIntegratePoint pt = new SimpMFBoundaryIntegratePoint();
                    pt.setCoord(qp.coord);
                    pt.setWeight(qp.weight);
                    pt.setLoad(value);
                    pt.setBoundary(lineBnd);
                    pt.setBoundaryParameter(qp.segmentParameter);
                    res.add(pt);
                }
            }
        }
        return res;
    }

    public void setModel(GeomModel model) {
        this.model = model;
        if (model.getDimension() != 2) {
            throw new IllegalArgumentException();
        }
        bndSearcher = new GenericSegmentLRTreeSearcher<>();
        bndSearcher.setSegmentGetter(MFLineBnd.segmentGetter());
        bndSearcher.setDimension(2);
        bndSearcher.setDatas((List) model.getBoundaries());
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
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
