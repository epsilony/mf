/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2DUtils {

    public static Polygon2D<MFNode> clonePolygonWithMFNode(Polygon2D polygon) {
        ArrayList<Line<MFNode>> newChainsHeads = clonePolygonWithMFNode(polygon.getChainsHeads());
        Polygon2D<MFNode> result = new Polygon2D<>();
        result.setChainsHeads(newChainsHeads);
        return result;
    }

    public static ArrayList<Line<MFNode>> clonePolygonWithMFNode(List<Line> chainsHeads) {
        ArrayList<Line<MFNode>> newChainsHeads = new ArrayList<>(chainsHeads.size());
        for (Line head : chainsHeads) {
            SegmentIterator<Line> iter = new SegmentIterator<>(head);
            Line newHead = new Line();
            Line oldHead = iter.next();
            MFNode newHeadStart = new MFNode(oldHead.getStartCoord());
            newHeadStart.setAsStart(newHead);
            newHead.setStart(newHeadStart);
            Line pred = newHead;
            newChainsHeads.add(newHead);
            while (iter.hasNext()) {
                Line newLine = new Line();
                Line oldLine = iter.next();
                MFNode newStart = new MFNode(oldLine.getStartCoord());
                newStart.setAsStart(newLine);
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

    public static List<MFNode> getAllGeomNodes(GeomModel2D md) {
        LinkedList<MFNode> result = new LinkedList<>(md.spaceNodes);
        if (null == md.getPolygon()) {
            return result;
        }
        for (Line<MFNode> seg : md.getPolygon()) {
            result.add(seg.getStart());
        }
        return result;
    }
}
