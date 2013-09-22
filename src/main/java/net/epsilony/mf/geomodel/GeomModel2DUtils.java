/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.GeneralPolygon2D;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2DUtils {

    public static GeneralPolygon2D<MFLine, MFNode> clonePolygonWithMFNode(GeneralPolygon2D polygon) {
        ArrayList<MFLine> newChainsHeads = clonePolygonWithMFNode(polygon.getChainsHeads());
        GeneralPolygon2D<MFLine, MFNode> result = new GeneralPolygon2D<>();
        result.setChainsHeads(newChainsHeads);
        return result;
    }

    public static ArrayList<MFLine> clonePolygonWithMFNode(List<Line> chainsHeads) {
        ArrayList<MFLine> newChainsHeads = new ArrayList<>(chainsHeads.size());
        for (Line head : chainsHeads) {
            SegmentIterator<Line> iter = new SegmentIterator<>(head);
            MFLine newHead = new MFLine();
            Line oldHead = iter.next();
            MFNode newHeadStart = new MFNode(oldHead.getStartCoord());
            newHeadStart.setAsStart(newHead);
            newHead.setStart(newHeadStart);
            MFLine pred = newHead;
            newChainsHeads.add(newHead);
            while (iter.hasNext()) {
                MFLine newLine = new MFLine();
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
        for (MFLine seg : md.getPolygon()) {
            result.add(seg.getStart());
        }
        return result;
    }
}
