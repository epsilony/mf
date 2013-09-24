/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.ArrayList;
import java.util.List;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLineBnd implements MFBoundary {

    Line line;
    int id;

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
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
}
