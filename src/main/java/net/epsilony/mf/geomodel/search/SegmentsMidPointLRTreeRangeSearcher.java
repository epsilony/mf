/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.Line2D;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.DoubleArrayComparator;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.rangesearch.LayeredRangeTree;
import net.epsilony.tb.rangesearch.RangeSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentsMidPointLRTreeRangeSearcher implements RangeSearcher<double[], Segment> {

    public static final int DEFAULT_DIMENSION = 2;
    LayeredRangeTree<double[], Segment> segmentsTree;

    public SegmentsMidPointLRTreeRangeSearcher(Iterable<? extends Segment> segments, int dimension) {
        LinkedList<WithPair<double[], Segment>> midSegPairs = new LinkedList<>();
        for (Segment seg : segments) {
            PairPack<double[], Segment> midSegPair = new PairPack<>(Segment2DUtils.chordMidPoint(seg, null), seg);
            midSegPairs.add(midSegPair);
        }
        ArrayList<Comparator<double[]>> comps = new ArrayList<>(2);
        for (int i = 0; i < dimension; i++) {
            comps.add(new DoubleArrayComparator(i));
        }
        segmentsTree = new LayeredRangeTree<>(midSegPairs, comps);
    }

    public SegmentsMidPointLRTreeRangeSearcher(Iterable<? extends Line2D> segments) {
        this(segments, DEFAULT_DIMENSION);
    }

    @Override
    public List<Segment> rangeSearch(double[] from, double[] to) {
        return segmentsTree.rangeSearch(from, to);
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this);
    }
}
