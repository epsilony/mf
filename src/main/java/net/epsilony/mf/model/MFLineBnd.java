/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.model.search.SegmentGetter;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLineBnd extends AbstractMFBoundary implements MFBoundary {

    Line line;

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public MFLineBnd() {
    }

    public MFLineBnd(Line line) {
        this.line = line;
    }

    public static List<MFLineBnd> wraps(List<? extends Segment> lines) {
        List<MFLineBnd> result = new ArrayList<>(lines.size());
        for (Segment seg : lines) {
            result.add(new MFLineBnd((Line) seg));
        }
        return result;
    }

    public static List<Line> fectchLines(List<? extends MFBoundary> bnds) {
        ArrayList<Line> result = new ArrayList<>(bnds.size());
        for (MFBoundary bnd : bnds) {
            result.add(((MFLineBnd) bnd).getLine());
        }
        return result;
    }

    public static SegmentGetter<MFLineBnd> segmentGetter() {
        return new SegmentGetter<MFLineBnd>() {
            @Override
            public Segment getSegment(MFLineBnd v) {
                return v.getLine();
            }
        };
    }

    @Override
    public Line getGeomUnit() {
        return line;
    }

    @Override
    public void setGeomUnit(GeomUnit geomUnit) {
        line = (Line) geomUnit;
    }
}
