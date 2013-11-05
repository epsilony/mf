/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LineIntegratePointsFactory implements Factory<List<MFIntegratePoint>> {

    public static final int DEFAULT_DEGREE = 2;
    Line startLine, endLine;
    double startParameter = 0, endParameter = 1;
    double[] startCoord;
    double[] endCoord;
    Map<GeomUnit, MFLoad> loadMap;
    boolean fetchLoadRecursively = false;
    private LinearIntegratePointsFactory linearIntegratePointsFactory;

    public LineIntegratePointsFactory() {
        linearIntegratePointsFactory = new LinearIntegratePointsFactory();
        linearIntegratePointsFactory.setDegree(DEFAULT_DEGREE);
    }

    @Override
    public List<MFIntegratePoint> produce() {

        genStartEndCoord();
        linearIntegratePointsFactory.setStartCoord(startCoord);
        linearIntegratePointsFactory.setEndCoord(endCoord);

        List<MFIntegratePoint> semiFinishedPoints = linearIntegratePointsFactory.produce();

        List<MFIntegratePoint> results = new LinkedList<>();
        for (MFIntegratePoint semiPoint : semiFinishedPoints) {
            results.add(genPoint(semiPoint));
        }
        return results;
    }

    private void genStartEndCoord() {
        startLine.setDiffOrder(0);
        startCoord = startLine.values(startParameter, null);
        if (null == endLine) {
            endCoord = startLine.values(endParameter, null);
        } else {
            endLine.setDiffOrder(0);
            endCoord = endLine.values(endParameter, null);
        }
    }

    private RawMFBoundaryIntegratePoint genPoint(MFIntegratePoint semiFinishedPoint) {
        RawMFBoundaryIntegratePoint result = newPointCopy(semiFinishedPoint);

        Line line = getLineWhereCoordAt(result.getCoord());
        double lineParameter = getLineParamenterByCoord(line, result.getCoord());
        result.setBoundary(line);
        result.setBoundaryParameter(lineParameter);

        if (null != loadMap) {
            SegmentLoad load = getLineLoad(line);
            load.setParameter(lineParameter);
            load.setSegment(line);
            result.setLoad(load.getLoad());
            result.setLoadValidity(load.getLoadValidity());
        }
        return result;
    }

    private RawMFBoundaryIntegratePoint newPointCopy(MFIntegratePoint semiFinishedPoint) {
        RawMFBoundaryIntegratePoint result = new RawMFBoundaryIntegratePoint();
        result.setCoord(semiFinishedPoint.getCoord());
        result.setWeight(semiFinishedPoint.getWeight());
        return result;
    }

    private Line getLineWhereCoordAt(double[] coord) {
        Line line = startLine;
        if (null != endLine && startLine != endLine) {
            double coordToStart = Math2D.distanceSquare(coord, startLine.getStartCoord());
            do {
                double lineEndToStart = Math2D.distanceSquare(line.getEndCoord(), startLine.getStartCoord());
                if (coordToStart > lineEndToStart) {
                    line = (Line) line.getSucc();
                } else {
                    break;
                }
            } while (line != endLine);

        }
        return line;

    }

    private double getLineParamenterByCoord(Line line, double[] coord) {
        double lineParameter = Math2D.distance(coord, line.getStartCoord()) / line.length();
        return lineParameter;
    }

    private SegmentLoad getLineLoad(Line line) {
        SegmentLoad load = (SegmentLoad) loadMap.get(line);
        if (null == load && fetchLoadRecursively) {
            GeomUnit loadUnit = line.getParent();
            while (load == null && loadUnit != null) {
                load = (SegmentLoad) loadMap.get(loadUnit);
                loadUnit = loadUnit.getParent();
            }
        }
        if (null == load) {
            throw new IllegalStateException();
        }
        return load;
    }

    public Line getStartLine() {
        return startLine;
    }

    public void setStartLine(Line startLine) {
        this.startLine = startLine;
    }

    public Line getEndLine() {
        return endLine;
    }

    public void setEndLine(Line endLine) {
        this.endLine = endLine;
    }

    public double getStartParameter() {
        return startParameter;
    }

    public void setStartParameter(double startParameter) {
        this.startParameter = startParameter;
    }

    public double getEndParameter() {
        return endParameter;
    }

    public void setEndParameter(double endParameter) {
        this.endParameter = endParameter;
    }

    public Map<GeomUnit, MFLoad> getLoadMap() {
        return loadMap;
    }

    public void setLoadMap(Map<GeomUnit, MFLoad> loadMap) {
        this.loadMap = loadMap;
    }

    public boolean isFetchLoadRecursively() {
        return fetchLoadRecursively;
    }

    public void setFetchLoadRecursively(boolean fetchLoadRecursively) {
        this.fetchLoadRecursively = fetchLoadRecursively;
    }

    public void setDegree(int degree) {
        linearIntegratePointsFactory.setDegree(degree);
    }

    public int getNumOfPoints() {
        return linearIntegratePointsFactory.getNumOfPoints();
    }

    public int getDegree() {
        return linearIntegratePointsFactory.getDegree();
    }
}
