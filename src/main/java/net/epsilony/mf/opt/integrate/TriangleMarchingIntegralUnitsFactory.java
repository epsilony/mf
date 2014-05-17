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
package net.epsilony.mf.opt.integrate;

import java.util.Collection;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.implicit.contour.InTriangleEdgeToPolygon;
import net.epsilony.mf.implicit.contour.TriangleMarching;
import net.epsilony.mf.implicit.contour.TriangleMarchingInnerPredicate;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleMarchingIntegralUnitsFactory {

    private Collection<? extends MFCell> cells;

    private TriangleMarching triangleMarching;

    private final InTriangleEdgeToPolygon inTriangleEdgeToPolygon = new InTriangleEdgeToPolygon();

    private final TriangleMarchingInnerPredicate innerCellPredicate = new TriangleMarchingInnerPredicate();

    private List<MFLine> contourLines;

    private List<MFEdge> contourHeads;

    public void generateUnits() {
        contourHeads = triangleMarching.buildContour(cells);
        contourLines = contourHeads.stream().flatMap(MFLine::stream).collect(Collectors.toList());
    }

    public List<MFLine> boundaryUnits() {
        return contourLines;
    }

    public List<PolygonIntegrateUnit> volumeUnits() {
        List<PolygonIntegrateUnit> bndVols = contourLines.stream()
                .map(line -> inTriangleEdgeToPolygon.apply((MFEdge) line)).filter(qu -> qu != null)
                .collect(Collectors.toList());

        List<PolygonIntegrateUnit> vols = cells.stream().filter(innerCellPredicate).map(this::cellToUnit)
                .collect(Collectors.toList());
        vols.addAll(bndVols);
        return vols;
    }

    private PolygonIntegrateUnit cellToUnit(MFCell cell) {
        PolygonIntegrateUnit unit = new PolygonIntegrateUnit();
        double[][] coords = new double[cell.vertesSize()][];
        for (int i = 0; i < cell.vertesSize(); i++) {
            coords[i] = cell.getVertexCoord(i);
        }
        unit.setVertesCoords(coords);
        return unit;
    }

    public void setCells(Collection<? extends MFCell> cells) {
        this.cells = cells;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        inTriangleEdgeToPolygon.setLevelFunction(levelFunction);
        innerCellPredicate.setLevelFunction(levelFunction);
    }

    public void setTriangleMarching(TriangleMarching triangleMarching) {
        this.triangleMarching = triangleMarching;
    }

    public List<MFEdge> getContourHeads() {
        return contourHeads;
    }

}
