/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2DUtils {

    public static Facet clonePolygonWithMFNode(Facet facet) {
        ArrayList<Line> newChainsHeads = clonePolygonWithMFNode(facet.getRingsHeads());
        return Facet.byRingsHeads(newChainsHeads);
    }

    public static ArrayList<Line> clonePolygonWithMFNode(List<? extends Segment> chainsHeads) {
        ArrayList<Line> newChainsHeads = new ArrayList<>(chainsHeads.size());
        for (Segment head : chainsHeads) {
            SegmentIterator<Segment> iter = new SegmentIterator<>(head);
            Line newHead = new Line();
            Segment oldHead = iter.next();
            MFNode newHeadStart = new MFNode(oldHead.getStart().getCoord());
            newHead.setStart(newHeadStart);
            Line pred = newHead;
            newChainsHeads.add(newHead);
            while (iter.hasNext()) {
                Line newLine = new Line();
                Segment oldLine = iter.next();
                MFNode newStart = new MFNode(oldLine.getStart().getCoord());
                newLine.setStart(newStart);
                Segment2DUtils.link(pred, newLine);
                pred = newLine;
            }
            if (oldHead.getPred() != null) {
                Segment2DUtils.link(pred, newHead);
            }
        }

        return newChainsHeads;
    }

    public static List<MFNode> getAllGeomNodes(AnalysisModel md) {
        LinkedList<MFNode> result = new LinkedList<>(md.getSpaceNodes());
        GeomUnit geomRoot = md.getFractionizedModel().getGeomRoot();
        if (null == geomRoot) {
            return result;
        }
        for (Segment seg : (Facet) geomRoot) {
            result.add((MFNode) seg.getStart());
        }
        return result;
    }

    public static List<GeomUnit> getAllSegments(GeomUnit geomRoot) {
        LinkedList<GeomUnit> result = new LinkedList<>();
        if (geomRoot instanceof Facet) {
            Facet facet = (Facet) geomRoot;
            for (Segment seg : facet) {
                result.add(seg);
            }
            return result;
        }

        throw new IllegalArgumentException();
    }
}
