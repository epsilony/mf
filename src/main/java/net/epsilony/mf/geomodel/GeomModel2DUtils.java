/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.ArrayList;
import java.util.List;
import net.epsilony.tb.solid.Line2D;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2DUtils {

    public static Polygon2D<MFNode> clonePolygonWithMFNode(Polygon2D polygon) {
        ArrayList<Line2D<MFNode>> newChainsHeads = clonePolygonWithMFNode(polygon.getChainsHeads());
        Polygon2D<MFNode> result = new Polygon2D<>();
        result.setChainsHeads(newChainsHeads);
        return result;
    }

    public static ArrayList<Line2D<MFNode>> clonePolygonWithMFNode(List<Line2D> chainsHeads) {
        ArrayList<Line2D<MFNode>> newChainsHeads = new ArrayList<>(chainsHeads.size());
        for (Line2D head : chainsHeads) {
            SegmentIterator<Line2D> iter = new SegmentIterator<>(head);
            Line2D newHead = new Line2D();
            Line2D oldHead = iter.next();
            MFNode newHeadStart = new MFNode(oldHead.getStartCoord());
            newHeadStart.setAsStart(newHead);
            newHead.setStart(newHeadStart);
            Line2D pred = newHead;
            newChainsHeads.add(newHead);
            while (iter.hasNext()) {
                Line2D newLine = new Line2D();
                Line2D oldLine = iter.next();
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
}
