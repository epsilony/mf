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
package net.epsilony.mf.util.function;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ParallelDispatcherTest {

    @Test
    public void testSum() {
        int num = 100;
        int threadNum = 10;
        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            data.add(i + 1);
        }
        Collections.shuffle(data);

        int exp = (1 + num) * num / 2;
        ArrayList<Consumer<Integer>> consumers = new ArrayList<>();

        int[] results = new int[threadNum];
        for (int i = 0; i < threadNum; i++) {
            int index = i;
            consumers.add(arg -> results[index] += arg);
        }
        ParallelDispatcher<Integer> sample = new ParallelDispatcher<Integer>(data.spliterator(), consumers);
        sample.run();
        int sum = 0;
        for (int i : results) {
            sum += i;
        }
        assertEquals(exp, sum);
    }
}
