/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Arrays;
import java.util.HashMap;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import java.util.List;
import java.util.Map.Entry;
import net.epsilony.mf.model.MFBoundary;
import net.epsilony.mf.model.MFLineBnd;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.Rectangle2DModel;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.solid.Segment2DUtils;
import static net.epsilony.mf.model.MFRectangleEdge.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractRectangleTask {

    protected Rectangle2DModel rectangle2DModel;
    protected Common2DTask model2DTask = new Common2DTask();
    protected double minBoundaryLength;
    protected boolean needPrepare = true;
    int id; // not delete this because of hibernate

    public void setRectangleModel(Rectangle2DModel rectangle2DModel) {
        needPrepare = true;
        this.rectangle2DModel = rectangle2DModel;
    }
    HashMap<MFRectangleEdge, List<GenericFunction>> boundaryConditions = new HashMap<>(4);

    public void addBoundaryConditionOnEdge(MFRectangleEdge edge, GenericFunction<double[], double[]> value, GenericFunction<double[], boolean[]> diriMark) {
        boundaryConditions.put(edge, (List) Arrays.asList(value, diriMark));
        needPrepare = true;
    }

    protected void prepare() {
        if (!needPrepare) {
            return;
        }

        double minLen = Double.POSITIVE_INFINITY;
        for (MFBoundary bnd : rectangle2DModel.getBoundaries()) {
            double len = Segment2DUtils.chordLength(((MFLineBnd) bnd).getLine());
            if (len < minLen) {
                minLen = len;
            }
        }
        minBoundaryLength = minLen;
        model2DTask.setBoundaries(rectangle2DModel.getBoundaries());
        for (Entry<MFRectangleEdge, List<GenericFunction>> entry : boundaryConditions.entrySet()) {
            applyBC(entry.getKey(), entry.getValue().get(0), entry.getValue().get(1));
        }

        prepareVolume();
        needPrepare = false;
    }

    protected abstract void prepareVolume();

    protected void applyBC(MFRectangleEdge edge, GenericFunction<double[], double[]> value, GenericFunction<double[], boolean[]> diriMark) {
        double l;
        double d;
        double r;
        double u;
        double left = rectangle2DModel.getLeft();
        double right = rectangle2DModel.getRight();
        double down = rectangle2DModel.getDown();
        double up = rectangle2DModel.getUp();
        double t = minBoundaryLength / 10;
        switch (edge) {
            case LEFT:
                l = left - t;
                d = down;
                r = left + t;
                u = up;
                break;
            case DOWN:
                l = left;
                r = right;
                d = down - t;
                u = down + t;
                break;
            case RIGHT:
                l = right - t;
                r = right + t;
                d = down;
                u = up;
                break;
            case UP:
                l = left;
                r = right;
                d = up - t;
                u = up + t;
                break;
            default:
                throw new IllegalArgumentException("The edge should only be one of \n" + "[ \"lelf\", \"l\", \"d\", \"down\", \"r\", \"right\", \"u\", \"up\" ]\n" + "but get :" + edge);
        }
        double[] from = new double[]{l, d};
        double[] to = new double[]{r, u};
        if (diriMark != null) {
            model2DTask.addDirichletBoundaryCondition(from, to, value, diriMark);
        } else {
            model2DTask.addNeumannBoundaryCondition(from, to, value);
        }
    }

    public List<MFBoundaryIntegratePoint> dirichletTasks() {
        prepare();
        return model2DTask.dirichletTasks();
    }

    public List<MFBoundaryIntegratePoint> neumannTasks() {
        prepare();
        return model2DTask.neumannTasks();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        needPrepare = true;
        model2DTask.setQuadratureDegree(quadratureDegree);
    }

    public int getQuadratureDegree() {
        return model2DTask.getQuadratureDegree();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
