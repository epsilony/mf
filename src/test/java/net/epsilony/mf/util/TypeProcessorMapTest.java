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
package net.epsilony.mf.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TypeProcessorMapTest {

    TypeProcessorMap typeProcessorMap = genSample();

    Map<Object, Class<?>> inputAndExps = genInputAndExp();

    TypeProcessorMap genSample() {
        TypeProcessorMap result = new TypeProcessorMap();
        result.register(genSampleRegistry());
        return result;
    }

    Map<Class<?>, Object> genSampleRegistry() {
        Map<Class<?>, Object> registry = new HashMap<>();
        registry.put(MockA.class, new MockProcessor(MockA.class));
        registry.put(MockC.class, new MockProcessor(MockC.class));
        registry.put(MockE.class, new MockProcessor(MockE.class));
        registry.put(MockII.class, new MockProcessor(MockII.class));
        return registry;
    }

    Map<Object, Class<?>> genInputAndExp() {
        Map<Object, Class<?>> inputAndExp = new HashMap<>();
        inputAndExp.put(new MockA(), MockA.class);
        inputAndExp.put(new MockB(), MockA.class);
        inputAndExp.put(new MockC(), MockC.class);
        inputAndExp.put(new MockD(), MockC.class);
        inputAndExp.put(new MockE(), MockE.class);
        inputAndExp.put("good", null);
        inputAndExp.put(new MockI(), null);
        inputAndExp.put(new MockII(), MockII.class);
        inputAndExp.put(new MockIII(), MockII.class);
        inputAndExp.put(new MockI2(), null);
        return inputAndExp;
    }

    @Test
    public void test() {
        for (Map.Entry<Object, Class<?>> entry : inputAndExps.entrySet()) {
            Object object = typeProcessorMap.get(entry.getKey().getClass());

            MockProcessor processor = (MockProcessor) object;
            if (entry.getValue() == null) {
                assertEquals(null, processor);
            } else {
                assertEquals(entry.getValue(), processor.forType);
            }

        }
    }

    static class MockA {

    }

    static class MockB extends MockA {

    }

    static class MockC extends MockB {

    }

    static class MockD extends MockC {

    }

    static class MockE extends MockD {

    }

    static class MockProcessor {
        public final Class<?> forType;

        public MockProcessor(Class<?> forType) {
            this.forType = forType;
        }

    }

    static class MockI {

    }

    static class MockII extends MockI {

    }

    static class MockIII extends MockII {

    }

    static class MockI2 extends MockI {

    }
}
