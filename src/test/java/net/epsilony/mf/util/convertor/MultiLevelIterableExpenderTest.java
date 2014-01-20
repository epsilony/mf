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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MultiLevelIterableExpenderTest {

    @Test
    public void testExpanding() {
        int[] numLevelss = new int[] { 1, 2, 3, 4 };
        int[] perLevelSizes = new int[] { 0, 1, 2, 3 };

        List<Sample> samples = new LinkedList<>();
        for (int numLevels : numLevelss) {
            for (int perLevelSize : perLevelSizes) {
                Sample sample = genSample(numLevels, perLevelSize);
                samples.add(sample);
            }
        }

        boolean meetNonEmptyTest = false;
        for (Sample sample : samples) {
            testBySample(sample);
            if (!sample.strings.isEmpty()) {
                meetNonEmptyTest = true;
            }
        }
        Assert.assertTrue(meetNonEmptyTest);
    }

    private void testBySample(Sample sample) {
        MultiLevelIterableExpender<String> expender = new MultiLevelIterableExpender<>();
        expender.setNumLevels(sample.numLevels);
        Iterable<String> act = expender.convert(sample.sampleMultiIterable);
        Iterator<String> expIter = sample.strings.iterator();
        Iterator<String> actIter = act.iterator();
        while (expIter.hasNext()) {
            String actStr = actIter.next();
            String expStr = expIter.next();
            // System.out.println("actStr = " + actStr);
            Assert.assertEquals(expStr, actStr);
        }
        Assert.assertTrue(!actIter.hasNext());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Sample genSample(int numLevels, int perLevelSize) {
        List<String> strings = new LinkedList<>();
        ArrayList stack = new ArrayList<>(numLevels);
        stack.add(new ArrayList());
        while (stack.size() <= numLevels) {
            List head = (List) stack.get(0);
            int headIndex = 0;
            while (head.size() >= perLevelSize && stack.size() <= numLevels) {
                headIndex++;
                if (headIndex >= stack.size()) {
                    head = new ArrayList();
                    stack.add(head);
                } else {
                    head = (List) stack.get(headIndex);
                }
                head.add(stack.get(headIndex - 1));
                stack.set(headIndex - 1, new ArrayList());
            }
            if (stack.size() > numLevels) {
                break;
            }
            String value = "";
            for (Object obj : stack) {
                value = ((List) obj).size() + value;
            }
            strings.add(value);
            head = (List) stack.get(0);
            head.add(value);
        }

        List sample = (List) ((List) stack.get(stack.size() - 1)).get(0);
        Sample result = new Sample();
        result.sampleMultiIterable = sample;
        result.numLevels = numLevels;
        result.strings = strings;
        return result;
    }

    public static class Sample {
        public List<String> strings;
        @SuppressWarnings("rawtypes")
        public List sampleMultiIterable;
        public int numLevels;
    }

}
