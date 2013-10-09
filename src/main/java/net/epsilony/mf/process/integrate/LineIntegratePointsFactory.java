/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.RawMFBoundaryIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.quadrature.GaussLegendre;
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
    int quadratureDegree = -1;
    double[] quadPoints;
    double[] quadWeights;
    double[] startCoord;
    double[] endCoord;
    Map<GeomUnit, MFLoad> loadMap;
    boolean fetchLoadRecursively = false;

    public LineIntegratePointsFactory() {
        setQuadratureDegree(DEFAULT_DEGREE);
    }

    @Override
    public List<MFIntegratePoint> produce() {
        ArrayList<MFIntegratePoint> results = new ArrayList<>(quadPoints.length);
        genStartEndCoord();
        for (int i = 0; i < quadPoints.length; i++) {
            results.add(genPoint(i));
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

    private RawMFBoundaryIntegratePoint genPoint(int index) {
        double quadPt = quadPoints[index];
        double quadWeight = quadWeights[index];
        double[] coord = Math2D.pointOnSegment(startCoord, endCoord, (1 + quadPt) / 2, null);
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
        double lineParmenter = Math2D.distance(coord, line.getStartCoord()) / line.length();
        RawMFBoundaryIntegratePoint result = new RawMFBoundaryIntegratePoint();
        result.setCoord(coord);
        result.setBoundary(line);
        result.setWeight(quadWeight / 2 * Math2D.distance(startCoord, endCoord));
        result.setBoundaryParameter(lineParmenter);

        if (null != loadMap) {
            SegmentLoad load = getLineLoad(line);
            load.setParameter(lineParmenter);
            load.setSegment(line);
            result.setLoad(load.getLoad());
            result.setLoadValidity(load.getLoadValidity());
        }
        return result;
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

    public double getQuadratureDegree() {
        return quadratureDegree;
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

    public void setQuadratureDegree(int quadratureDegree) {
        GaussLegendre.checkDegree(quadratureDegree);
        if (quadratureDegree == this.quadratureDegree) {
            return;
        }
        this.quadratureDegree = quadratureDegree;
        double[][] ptsWeights = GaussLegendre.pointsWeightsByDegree(quadratureDegree);
        quadPoints = ptsWeights[0];
        quadWeights = ptsWeights[1];
    }
}
