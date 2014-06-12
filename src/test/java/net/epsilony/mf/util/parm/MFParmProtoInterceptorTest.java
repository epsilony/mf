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
package net.epsilony.mf.util.parm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.epsilony.mf.util.parm.MFParmProtoInterceptor;
import net.epsilony.mf.util.parm.ann.MFParmIgnore;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmProtoInterceptorTest {

    @Test
    public void test() {
        int protoNum = 3;
        MFParmProtoInterceptor<SampleClass> interceptor = new MFParmProtoInterceptor<>(SampleClass.class);
        List<SampleClass> samples = new ArrayList<>();
        for (int i = 0; i < protoNum; i++) {
            samples.add(interceptor.newProto());
        }

        SampleClass parmProxy = interceptor.getParmProxy();
        boolean throwed = false;
        try {
            parmProxy.setA("any thing");
        } catch (Throwable e) {
            if (!(e instanceof IllegalStateException)) {
                throw e;
            }
            throwed = true;
        }
        assertTrue(throwed);

        parmProxy.setB("exp");
        for (SampleClass sample : samples) {
            assertEquals("exp", sample.getB());
        }
    }

    public static class SampleClass {

        String a, b;

        public String getA() {
            return a;
        }

        @MFParmIgnore
        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

    }

}
