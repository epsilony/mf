package net.epsilony.mf.integrate.integrator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.function.Function;

import org.junit.Test;

public class TypeMapConvertorCascadeIntegratorTest {

    int oneManySampleSize = 10;

    @Test
    public void test() {
        Set<BaseType> samples = new HashSet<>();
        samples.addAll(Arrays.asList(new MockA(), new MockB(), new MockA(), new MockB()));

        TypeMapConvertorCascadeIntegrator<BaseType, SubType> integrator = new TypeMapConvertorCascadeIntegrator<>();
        ListRecorderIntegrator<SubType> subIntegrator = new ListRecorderIntegrator<>();
        integrator.setSubIntegrator(subIntegrator);
        integrator.registerOneOne(MockA.class, new MockOneOneConvertor());
        integrator.registerOneMany(MockB.class, new MockOneManyConvertor());

        for (BaseType baseType : samples) {
            integrator.setIntegrateUnit(baseType);
            integrator.integrate();
        }

        int numSubType = 0;
        int numSubType2 = 0;
        Set<BaseType> recieved = new HashSet<>();
        for (SubType subType : subIntegrator.getRecords()) {
            if (subType instanceof SubType2) {
                numSubType2++;
            } else {
                numSubType++;
            }
            recieved.add(subType.beforeConvert);
        }

        int expNumSubType = 2;
        int expNumSubType2 = 20;
        // System.out.println("recieved = " + recieved);
        assertEquals(expNumSubType, numSubType);
        assertEquals(expNumSubType2, numSubType2);
        assertEquals(samples.size(), recieved.size());
        assertEquals(samples, recieved);
    }

    public static class BaseType {
    };

    public static class MockA extends BaseType {
    }

    public static class MockB extends BaseType {
    }

    public static class SubType {
        public final BaseType beforeConvert;

        public SubType(BaseType beforeConvert) {
            this.beforeConvert = beforeConvert;
        }
    }

    public static class SubType2 extends SubType {

        public SubType2(BaseType beforeConvert) {
            super(beforeConvert);
        }
    };

    public class MockOneManyConvertor implements Function<MockB, List<SubType2>> {

        @Override
        public List<SubType2> apply(MockB input) {
            ArrayList<SubType2> result = new ArrayList<>(oneManySampleSize);
            for (int i = 0; i < oneManySampleSize; i++) {
                result.add(new SubType2(input));
            }
            return result;
        }
    }

    public class MockOneOneConvertor implements Function<MockA, SubType> {

        @Override
        public SubType apply(MockA input) {
            return new SubType(input);
        }
    }
}
