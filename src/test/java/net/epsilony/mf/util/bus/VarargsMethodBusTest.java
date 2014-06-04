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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * 
 */
public class VarargsMethodBusTest {

    private static final List<Integer> AS_LIST  = Arrays.asList(1, 2, 3, 4);
    private static final int           expValue = 1024;
    int                                listInputTestCount;
    int                                noInputTestCount;
    int                                metaInputTestCount;

    @Test
    public void testObjectInput() {

        int mockSize = 3;
        VarargsMethodBus methodEventBus = new VarargsMethodBus(Collection.class);
        List<Mock> mocks = new LinkedList<>();
        for (int i = 0; i < mockSize; i++) {

            Mock mock = new Mock();
            mocks.add(mock);
            methodEventBus.register(mock, "inputList");
            methodEventBus.register(mock, "noInput");

        }
        System.gc();
        listInputTestCount = 0;
        noInputTestCount = 0;
        methodEventBus.post(AS_LIST);
        assertEquals(mockSize, listInputTestCount);
        assertEquals(mockSize, noInputTestCount);
    }

    @Test
    public void testMetaInput() {

        int mockSize = 3;
        VarargsMethodBus methodEventBus = new VarargsMethodBus(Integer.class);
        List<Mock> mocks = new LinkedList<>();
        for (int i = 0; i < mockSize; i++) {

            Mock mock = new Mock();
            mocks.add(mock);
            methodEventBus.register(mock, "metaInput");
        }
        System.gc();
        metaInputTestCount = 0;
        methodEventBus.post(expValue);
        assertEquals(mockSize, metaInputTestCount);
    }

    @Test
    public void testPostOnlyForNew() {
        int mockSize = 3;
        VarargsMethodBus methodBus = new VarargsMethodBus(int.class);
        List<Mock> mocks = new LinkedList<>();
        for (int i = 0; i < mockSize; i++) {

            Mock mock = new Mock();
            mocks.add(mock);
            methodBus.register(mock, "metaInput");
        }
        metaInputTestCount = 0;
        methodBus.post(expValue);
        for (int i = 0; i < mockSize; i++) {

            Mock mock = new Mock();
            mocks.add(mock);
            methodBus.register(mock, "metaInput");
        }
        int trivalPositiveCount = 4;
        for (int i = 0; i < trivalPositiveCount; i++) {
            methodBus.postToFresh(expValue);
        }
        assertEquals(mocks.size(), metaInputTestCount);
    }

    @Test
    public void testPostBySubEventBus() {
        int upperMockSize = 3;
        int subBusSize = 3;
        int lowerMockEachSize = 4;
        ArrayList<VarargsMethodBus> allEventBuses = new ArrayList<>(subBusSize + 1);
        for (int i = 0; i <= subBusSize; i++) {
            allEventBuses.add(new VarargsMethodBus(Integer.class));
            if (i > 0) {
                allEventBuses.get(0).registerSubEventBus(allEventBuses.get(i));
            }
        }
        List<Mock> mocks = new LinkedList<Mock>();
        for (int i = 0; i < upperMockSize; i++) {
            Mock mock = new Mock();
            mocks.add(mock);
            allEventBuses.get(0).register(mock, "metaInput");
        }
        for (int i = 0; i < (lowerMockEachSize + 1) / 2; i++) {
            for (int j = 1; j <= subBusSize; j++) {
                VarargsMethodBus methodEventBus = allEventBuses.get(j);
                Mock mock = new Mock();
                mocks.add(mock);
                methodEventBus.register(mock, "metaInput");
            }
        }
        metaInputTestCount = 0;
        allEventBuses.get(0).postToFresh(expValue);
        assertEquals(mocks.size(), metaInputTestCount);
        for (int i = 0; i < lowerMockEachSize / 2; i++) {
            for (int j = 1; j <= subBusSize; j++) {
                VarargsMethodBus methodEventBus = allEventBuses.get(j);
                Mock mock = new Mock();
                mocks.add(mock);
                methodEventBus.register(mock, "metaInput");
            }
        }
        allEventBuses.get(0).postToFresh(expValue);
        assertEquals(mocks.size(), metaInputTestCount);
    }

    @Test
    public void testWeakReference() {
        int sampleSize = 10;
        ArrayList<Integer> nullPositions = Lists.newArrayList(0, 3, 5, 6, 9);
        ArrayList<Mock> mocks = new ArrayList<>(sampleSize);
        VarargsMethodBus methodEventBus = new VarargsMethodBus(Set.class);
        for (int i = 0; i < sampleSize; i++) {
            Mock mock = new Mock();
            mock.setId(i);
            mocks.add(mock);
            methodEventBus.register(mock, "recordPost");
        }
        for (int nullPostion : nullPositions) {
            mocks.set(nullPostion, null);
        }
        System.gc();
        Set<Integer> postSet = new HashSet<>();
        Set<Integer> nullSet = new HashSet<>(nullPositions);
        methodEventBus.post(postSet);
        boolean tested = false;
        for (int i = 0; i < sampleSize; i++) {
            boolean inPost = postSet.contains(i);
            boolean inNull = nullSet.contains(i);
            assertTrue((inPost != inNull && (inPost || inNull)));
            tested = true;
        }
        assertEquals(sampleSize - nullPositions.size(), methodEventBus.listenerRegistry.size());
        assertTrue(tested);

    }

    public class Mock {
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int inputList(Iterable<Integer> input) {
            boolean tested = false;
            Iterator<Integer> inputIter = input.iterator();
            for (int i = 0; i < AS_LIST.size(); i++) {
                assertEquals(AS_LIST.get(i), inputIter.next());
                tested = true;
            }
            assertTrue(tested);
            listInputTestCount++;
            return listInputTestCount;
        }

        public void noInput() {
            noInputTestCount++;
        }

        public void metaInput(int input) {
            metaInputTestCount++;
            assertEquals(expValue, input);
        }

        public void recordPost(Set<Integer> record) {
            record.add(id);
        }
    }
}
