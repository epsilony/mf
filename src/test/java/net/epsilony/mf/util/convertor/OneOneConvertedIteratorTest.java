/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.epsilony.mf.util.convertor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneOneConvertedIteratorTest {

    @Test
    public void test() {
        int sampleLength = 10;

        OneOneConvertedIterator<A, B> iter = new OneOneConvertedIterator<>();
        iter.setConvertor(new MockConvertor());

        List<A> samples = genSamples(sampleLength);
        iter.setUpstream(samples.iterator());
        Iterator<A> sampleIter = samples.iterator();
        while (iter.hasNext()) {
            B b = iter.next();
            A actA = b.a;
            A expA = sampleIter.next();
            assertEquals(expA, actA);
        }
        assertTrue(!sampleIter.hasNext());
    }

    public List<A> genSamples(int length) {
        List<A> as = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            as.add(new A());
        }
        return as;
    }

    public static class MockConvertor implements Function<A, B> {
        @Override
        public B apply(A input) {
            return new B(input);
        }
    }

    public static class A {
    }

    public static class B {
        A a;

        public B(A a) {
            this.a = a;
        }
    }

}
