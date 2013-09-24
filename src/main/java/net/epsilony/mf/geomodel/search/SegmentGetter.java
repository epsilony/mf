/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.search;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <V>
 */
public interface SegmentGetter<V> {

    Segment getSegment(V v);
}
