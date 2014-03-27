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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 *
 */
public class HolderOneOffConsumerBusTest {

    @Test
    public void test() {
        int testSize = 20;
        int setIndex = 10;
        assertTrue(testSize > 2 && setIndex < testSize && setIndex > 1);
        int expValue = 7;

        ConsumerBus<Integer> bus = new ConsumerBus<>();
        List<Mock> samples = new ArrayList<>(testSize);
        for (int i = 0; i < testSize; i++) {
            Mock mock = new Mock();
            samples.add(mock);
            bus.register(mock::setValue);
            if (i == setIndex) {
                bus.postToFresh(expValue);
            }
        }
        boolean tested = false;
        for (Mock mock : samples) {
            assertEquals(expValue, mock.value);
            tested = true;
        }
        assertTrue(tested);
    }

    public static class Mock {
        int value;

        public void setValue(int value) {
            this.value = value;
        }
    }

}
