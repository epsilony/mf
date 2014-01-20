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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneManyConvertorIteratorTest {

    @Test
    public void test() {
        List<List<A>> samples = new LinkedList<>();
        samples.add(genSampleAs(0, 3, null));
        samples.add(genSampleAs(10, 1, null));
        samples.add(genSampleAs(10, 2, null));
        samples.add(genSampleAs(20, 5, null));
        for (List<A> sampleAs : samples) {
            testBySampleAs(sampleAs);
        }
    }

    public void testBySampleAs(List<A> as) {
        MockConvertor convertor = new MockConvertor();
        OneManyConvertorIterator<A, B> iterator = new OneManyConvertorIterator<>();
        iterator.setConvertor(convertor);
        iterator.setUpstream(as.iterator());
        List<B> bs = new LinkedList<>();
        for (A a : as) {
            bs.addAll(a.bs);
        }

        for (B expB : bs) {
            B actB = iterator.next();
            Assert.assertEquals(expB, actB);
            System.out.println("actB = " + actB);
        }
        Assert.assertTrue(!iterator.hasNext());
    }

    List<A> genSampleAs(int length, int bsSizeSup, Random rand) {
        if (null == rand) {
            rand = new Random();
        }
        List<A> as = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            as.add(new A(rand.nextInt(bsSizeSup)));
        }
        return as;
    }

    static class MockConvertor implements Convertor<A, List<B>> {

        @Override
        public List<B> convert(A input) {
            return input.bs;
        }
    }

    static class B {
    }

    static class A {
        List<B> bs;

        A(int size) {
            bs = new ArrayList<B>(size);
            for (int i = 0; i < size; i++) {
                bs.add(new B());
            }
        }
    }

}
