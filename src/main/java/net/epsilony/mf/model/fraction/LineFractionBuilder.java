/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.fraction;

import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LineFractionBuilder extends AbstractFractionBuilder {

    @Override
    public void fractionize() {
        Line line = (Line) segment;
        line.fractionize(getFracLinesNum(), nodeFactory);
    }

    public int getFracLinesNum() {
        double length = Segment2DUtils.chordLength(segment);
        return (int) Math.ceil(length / lengthCap);
    }
}
