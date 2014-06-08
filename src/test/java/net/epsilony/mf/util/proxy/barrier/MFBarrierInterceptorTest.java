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
package net.epsilony.mf.util.proxy.barrier;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFBarrierInterceptorTest {

    @Test
    public void test() {
        MFBarrierInterceptor<Sample> interceptor = new MFBarrierInterceptor<>(Sample.class);
        Sample s1 = interceptor.newInstance();
        Sample s2 = interceptor.newInstance();

        for (Sample sample : new Sample[] { s1, s2 }) {
            sample.triggerA1();
            assertEquals(1, sample.a);
            sample.triggerA2();
            assertEquals(7, sample.a);
            assertEquals(0, sample.b);

            sample.triggerB2();
            assertEquals(2, sample.b);
            sample.triggerB1();
            assertEquals(7, sample.a);
            assertEquals(15, sample.b);

        }
    }

    public static class Sample {
        int a, b;

        @MFBarrier
        public void triggerA1() {
            a += 1;
        }

        @MFBarrier
        public void triggerA2() {
            a += 2;
        }

        @MFBarrierInvoker
        public void invokerA4() {
            a += 4;
        }

        @MFBarrier(group = "b")
        public void triggerB1() {
            b += 1;
        }

        @MFBarrier(group = "b")
        public void triggerB2() {
            b += 2;
        }

        @MFBarrierInvoker(group = "b")
        public void invokerB4() {
            b += 4;
        }

        @MFBarrierInvoker(group = "b")
        public void invokerB8() {
            b += 8;
        }
    }

}
