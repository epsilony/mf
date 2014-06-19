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
package net.epsilony.mf.util.bus;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class WeakBusTest {

    public final Logger logger = LoggerFactory.getLogger(WeakBusTest.class);

    @Test
    public void testPostAndWeak() {
        List<Mock> mocks = new ArrayList<>();
        WeakBus<List<Mock>> weakBus = new WeakBus<>("postAndWeak");
        int sampleSize = 5;
        for (int i = 0; i < sampleSize; i++) {
            Mock mock = new Mock(i);
            mocks.add(mock);
            weakBus.register(Mock::addTo, mock);
        }

        ArrayList<Mock> postedMocks = new ArrayList<WeakBusTest.Mock>();
        weakBus.post(postedMocks);
        assertEquals(sampleSize, postedMocks.size());
        int expId = 0;
        for (Mock mock : postedMocks) {
            assertEquals(expId, mock.id);
            expId++;
        }

        mocks.clear();
        postedMocks.clear();
        System.gc();
        weakBus.post(postedMocks);
        if (!postedMocks.isEmpty()) {
            logger.warn("the weak bus seems not weak!");
        }
    }

    @Test
    public void testSubBus() {

        List<Mock> mocks = new ArrayList<>();
        WeakBus<Integer> rootBus = new WeakBus<>("root");
        WeakBus<Integer> subBus = new WeakBus<>("sub");
        rootBus.registerSubBus(WeakBus::postToEach, subBus);
        for (int i = 0; i < 10; i++) {
            Mock mock = new Mock(i);
            mocks.add(mock);
            subBus.register(Mock::setValue, mock);
        }

        rootBus.postToEach(new Supplier<Integer>() {
            int i = 0;

            @Override
            public Integer get() {
                return i++;
            }
        });

        for (int i = 0; i < mocks.size(); i++) {
            assertEquals(i, mocks.get(i).value);
        }
    }

    public static class Mock {
        public final int id;
        public int       value;

        public Mock(int id) {
            this.id = id;
        }

        void addTo(Collection<Mock> collection) {
            collection.add(this);
        }

        void setValue(int value) {
            this.value = value;
        }
    }

}
