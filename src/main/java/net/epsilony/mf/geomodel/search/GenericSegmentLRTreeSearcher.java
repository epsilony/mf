/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.rangesearch.RangeSearcher;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <V>
 */
public class GenericSegmentLRTreeSearcher<V> implements RangeSearcher<double[], V> {

    SegmentGetter<V> segmentGetter;
    int dimension = SegmentsMidPointLRTreeRangeSearcher.DEFAULT_DIMENSION;
    Iterable<? extends V> datas;
    boolean needPrepare = true;
    SegmentsMidPointLRTreeRangeSearcher midPointLRTreeRangeSearcher;

    public void setSegmentGetter(SegmentGetter<V> segmentGetter) {
        this.segmentGetter = segmentGetter;
        needPrepare = true;
    }

    public void setDimension(int dimension) {
        if (this.dimension == dimension) {
            return;
        }
        this.dimension = dimension;
        needPrepare = true;
    }

    public void setDatas(Iterable<? extends V> datas) {
        this.datas = datas;
        needPrepare = true;
    }

    private void prepare() {
        if (!needPrepare) {
            return;
        }
        LinkedList<SegmentLikeEntity<V>> wrappedDatas = new LinkedList<>();
        for (V v : datas) {
            wrappedDatas.add(new SegmentLikeEntity<>(v, segmentGetter.getSegment(v)));
        }
        midPointLRTreeRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(wrappedDatas, dimension);
        needPrepare = false;
    }

    @Override
    public List<V> rangeSearch(double[] from, double[] to) {
        prepare();

        List<Segment> wrappedResult = midPointLRTreeRangeSearcher.rangeSearch(from, to);
        ArrayList<V> result = new ArrayList<>(wrappedResult.size());
        for (Segment seg : wrappedResult) {
            SegmentLikeEntity<V> entity = (SegmentLikeEntity<V>) seg;
            result.add(entity.getKey());
        }
        return result;
    }

    static class SegmentLikeEntity<V> implements Segment {

        V key;
        Segment segment;

        public SegmentLikeEntity(V key, Segment segment) {
            this.key = key;
            this.segment = segment;
        }

        public V getKey() {
            return key;
        }

        @Override
        public Node getStart() {
            return segment.getStart();
        }

        @Override
        public Segment getPred() {
            return segment.getPred();
        }

        @Override
        public Node getEnd() {
            return segment.getEnd();
        }

        @Override
        public Segment getSucc() {
            return segment.getSucc();
        }

        @Override
        public void setStart(Node start) {
            segment.setStart(start);
        }

        @Override
        public void setPred(Segment pred) {
            segment.setPred(pred);
        }

        @Override
        public void setSucc(Segment succ) {
            segment.setSucc(succ);
        }

        @Override
        public void bisect() {
            segment.bisect();
        }

        @Override
        public double[] values(double x, double[] results) {
            return segment.values(x, results);
        }

        @Override
        public int getId() {
            return segment.getId();
        }

        @Override
        public void setId(int id) {
            segment.setId(id);
        }

        @Override
        public int getDiffOrder() {
            return segment.getDiffOrder();
        }

        @Override
        public void setDiffOrder(int diffOrder) {
            segment.setDiffOrder(diffOrder);
        }
    }
}
