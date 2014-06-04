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
package net.epsilony.mf.adapt;

import static org.apache.commons.math3.util.FastMath.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLineIterator;
import net.epsilony.mf.util.math.VectorMath;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleCellFissionizer implements CellFissionizer {
    private Predicate<MFCell>                  fissionEnablePredicate;
    private Function<MFCell, MFCell>           fissionObstructorSearcher;
    private Supplier<? extends MFEdge>         edgeFactory;
    private Function<double[], ? extends Node> nodeFactory;
    private Supplier<? extends MFCell>         cellFactory;

    private MFCell                             cell;
    private final MFEdge[]                     sideMids     = new MFEdge[3];
    private static final double                errorRatio   = 1e-12;

    private final SimpFissionResult            record       = new SimpFissionResult(new ArrayList<>(4),
                                                                    new ArrayList<>(3), null);
    private final MFLineIterator<MFEdge>       edgeIterator = new MFLineIterator<MFEdge>(MFEdge.class);

    @Override
    public void setCell(MFCell cell) {
        Objects.requireNonNull(cell);
        if (cell.vertesSize() != 3) {
            throw new IllegalArgumentException();
        }
        this.cell = cell;
    }

    @Override
    public void fission() {
        final List<MFCell> newCellsRecord = record.getNewCells();
        newCellsRecord.clear();
        record.getNewNodes().clear();
        record.setFissioned(null);

        for (int i = 0; i < 3; i++) {
            sideMids[i] = searchOrCreateSideMid(i);
        }

        MFCell newCenter = cellFactory.get();
        for (int i = 0; i < 3; i++) {
            Node start = sideMids[(i + 1) % 3].getStart();
            MFEdge newSide = edgeFactory.get();
            newSide.setStart(start);
            newCenter.setVertexEdge(i, newSide);
        }
        newCenter.connectEdges();

        MFCell newCell = cellFactory.get();
        newCell.setVertexEdge(0, cell.getVertexEdge(0));
        newCell.setVertexEdge(1, newOppositeEdge(newCenter.getVertexEdge(1)));
        newCell.setVertexEdge(2, sideMids[2]);
        newCell.connectEdges();
        newCellsRecord.add(newCell);

        newCell = cellFactory.get();
        newCell.setVertexEdge(0, sideMids[0]);
        newCell.setVertexEdge(1, cell.getVertexEdge(1));
        newCell.setVertexEdge(2, newOppositeEdge(newCenter.getVertexEdge(2)));
        newCell.connectEdges();
        newCellsRecord.add(newCell);

        newCell = cellFactory.get();
        newCell.setVertexEdge(0, newOppositeEdge(newCenter.getVertexEdge(0)));
        newCell.setVertexEdge(1, sideMids[1]);
        newCell.setVertexEdge(2, cell.getVertexEdge(2));
        newCell.connectEdges();
        newCellsRecord.add(newCell);

        newCellsRecord.add(newCenter);

        record.setFissioned(cell);
    }

    private MFEdge newOppositeEdge(MFEdge vertexEdge) {
        MFEdge edge = edgeFactory.get();
        edge.setStart(vertexEdge.getEnd());
        edge.connectOpposite(vertexEdge);
        return edge;
    }

    private MFEdge searchOrCreateSideMid(int i) {

        final MFEdge vertexEdgeStart = cell.getVertexEdge(i);
        final MFEdge vertexEdgeEnd = cell.getVertexEdgeCycly(i + 1);
        MFEdge midEdge = null;
        if (vertexEdgeStart.getSucc() == vertexEdgeEnd) {
            midEdge = bisectSide(i);
        } else {
            edgeIterator.setup(vertexEdgeStart, vertexEdgeEnd.getSucc());
            double[] start = cell.getVertexCoord(i);
            double[] end = cell.getVertexCoord((i + 1) % 3);
            double expLenSq = VectorMath.distanceSquare(start, end) / 4;
            double error = expLenSq * errorRatio;

            while (edgeIterator.hasNext()) {
                midEdge = edgeIterator.next();
                double[] coord = midEdge.getStartCoord();
                double ds = VectorMath.distanceSquare(coord, start);
                if (abs(ds - expLenSq) < error) {
                    break;
                }
            }
        }
        return midEdge;
    }

    private MFEdge bisectSide(int i) {
        MFEdge side = cell.getVertexEdge(i);

        double[] mid = VectorMath.midPoint(side.getStartCoord(), side.getEndCoord());
        final Node newNode = nodeFactory.apply(mid);
        record.getNewNodes().add(newNode);

        MFEdge newEdge = edgeFactory.get();
        newEdge.setCell(cell);
        newEdge.setStart(newNode);

        MFLine succ = side.getSucc();
        side.connectSucc(newEdge);
        newEdge.connectSucc(succ);

        MFEdge opp = side.getOpposite();
        if (null != opp) {
            MFCell oppCell = opp.getCell();
            MFEdge newOpp = edgeFactory.get();
            newOpp.setCell(oppCell);
            newOpp.setStart(newNode);

            MFLine oppSucc = opp.getSucc();
            opp.connectSucc(newOpp);
            newOpp.connectSucc(oppSucc);

            side.connectOpposite(newOpp);
            newEdge.connectOpposite(opp);
        }

        return newEdge;

    }

    @Override
    public void record(Consumer<? super FissionResult> recorder) {
        recorder.accept(record);
    }

    @Override
    public MFCell nextFissionObstructor() {
        if (null == fissionObstructorSearcher) {
            return null;
        } else {
            return fissionObstructorSearcher.apply(cell);
        }
    }

    @Override
    public boolean isEnabledToFission() {
        if (null == fissionEnablePredicate) {
            return true;
        } else {
            return fissionEnablePredicate.test(cell);
        }
    }

    public void setEdgeFactory(Supplier<? extends MFEdge> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public void setCellFactory(Supplier<? extends MFCell> cellFactory) {
        this.cellFactory = cellFactory;
    }

}
