/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import no.uib.cipr.matrix.VectorEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFVector extends Iterable<VectorEntry> {

    double get(int index);

    void set(int index, double value);

    int size();
}
