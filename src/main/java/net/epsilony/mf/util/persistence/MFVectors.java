/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.util.Iterator;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFVectors {

    public static MFVector wrap(Vector vec) {
        return new VectorWapper(vec);
    }

    static class VectorWapper implements MFVector {

        Vector vector;

        public VectorWapper(Vector vector) {
            this.vector = vector;
        }

        @Override
        public double get(int index) {
            return vector.get(index);
        }

        @Override
        public void set(int index, double value) {
            vector.set(index, value);
        }

        @Override
        public int size() {
            return vector.size();
        }

        @Override
        public Iterator<VectorEntry> iterator() {
            return vector.iterator();
        }
    }
}
