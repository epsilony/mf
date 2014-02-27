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
package net.epsilony.mf.integrate.integrator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MultiThreadIterableIntegratorTest {

    @Test
    public void testByBlackBoxWithManyThread() {
        int sampleSize = 1000;
        int threadNum = 100;
        List<Integer> sampleUnits = genSampleUnits(sampleSize);
        MockIterationCompletedListener mockListener = genSampleMockListener(threadNum);

        MultiThreadIterableIntegrator<Integer> integrator = new MultiThreadIterableIntegrator<>();
        integrator.setIntegrateUnit(sampleUnits);
        integrator.setSubIntegrators(mockListener.getMockIntegrators());
        integrator.registryCompletedListener(mockListener, "iterationCompleted");
        integrator.integrate();

        int exp = 1;
        for (int act : mockListener.getAllIntegrated()) {
            assertEquals(exp, act);
            exp++;
        }

        assertEquals(sampleSize, integrator.getIntegratedCount());
    }

    private List<Integer> genSampleUnits(int sampleSize) {
        List<Integer> result = new ArrayList<>(sampleSize);
        for (int i = 1; i <= sampleSize; i++) {
            result.add(i);
        }
        return result;
    }

    private MockIterationCompletedListener genSampleMockListener(int threadNum) {
        List<MockIntegrator> mockIntegrators = new LinkedList<>();
        for (int i = 0; i < threadNum; i++) {
            mockIntegrators.add(new MockIntegrator());
        }
        return new MockIterationCompletedListener(mockIntegrators);
    }

    public static class MockIterationCompletedListener {
        List<MockIntegrator> mockIntegrators;
        List<Integer> allIntegrated;

        public MockIterationCompletedListener(List<MockIntegrator> mockIntegrators) {
            this.mockIntegrators = mockIntegrators;
        }

        public void iterationCompleted() {
            allIntegrated = new LinkedList<>();
            for (MockIntegrator mockIntegrator : mockIntegrators) {
                allIntegrated.addAll(mockIntegrator.getIntegrated());
            }
            Collections.sort(allIntegrated);
        }

        public List<MockIntegrator> getMockIntegrators() {
            return mockIntegrators;
        }

        public void setMockIntegrators(List<MockIntegrator> mockIntegrators) {
            this.mockIntegrators = mockIntegrators;
        }

        public List<Integer> getAllIntegrated() {
            return allIntegrated;
        }

        public void setAllIntegrated(List<Integer> allIntegrated) {
            this.allIntegrated = allIntegrated;
        }

    }

    public static class MockIntegrator extends AbstractIntegrator<Integer> {
        List<Integer> integrated = new LinkedList<Integer>();

        public List<Integer> getIntegrated() {
            return integrated;
        }

        public void setIntegrated(List<Integer> integrated) {
            this.integrated = integrated;
        }

        @Override
        public void integrate() {
            integrated.add(unit);
        }
    }

}
