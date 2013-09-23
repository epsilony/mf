/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.GeneralPolygon2D;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2DUtils {

    public static GeneralPolygon2D clonePolygonWithMFNode(GeneralPolygon2D polygon) {
        ArrayList<MFLine> newChainsHeads = clonePolygonWithMFNode(polygon.getChainsHeads());
        GeneralPolygon2D result = new GeneralPolygon2D();
        result.setChainsHeads(newChainsHeads);
        return result;
    }

    public static ArrayList<MFLine> clonePolygonWithMFNode(List<? extends Segment> chainsHeads) {
        ArrayList<MFLine> newChainsHeads = new ArrayList<>(chainsHeads.size());
        for (Segment head : chainsHeads) {
            SegmentIterator<Segment> iter = new SegmentIterator<>(head);
            MFLine newHead = new MFLine();
            Segment oldHead = iter.next();
            MFNode newHeadStart = new MFNode(oldHead.getStart().getCoord());
            newHeadStart.setAsStart(newHead);
            newHead.setStart(newHeadStart);
            MFLine pred = newHead;
            newChainsHeads.add(newHead);
            while (iter.hasNext()) {
                MFLine newLine = new MFLine();
                Segment oldLine = iter.next();
                MFNode newStart = new MFNode(oldLine.getStart().getCoord());
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
        for (Segment seg : md.getPolygon()) {
            result.add((MFNode) seg.getStart());
        }
        return result;
    }
}
