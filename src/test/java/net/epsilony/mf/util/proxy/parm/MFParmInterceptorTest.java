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
package net.epsilony.mf.util.proxy.parm;

import static org.junit.Assert.assertEquals;
import net.epsilony.mf.util.parm.MFParmBusPool;
import net.epsilony.mf.util.parm.MFParmInterceptor;
import net.epsilony.mf.util.parm.ann.MFParmBusAlias;
import net.epsilony.mf.util.parm.ann.MFParmBusTrigger;
import net.epsilony.mf.util.parm.ann.MFParmWithBusPool;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmInterceptorTest {

    @MFParmWithBusPool
    public static class ForTrigger {

        String a, b, c, d;

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public String getC() {
            return c;
        }

        @MFParmBusAlias("alias")
        public String getD() {
            return d;
        }

        @MFParmBusTrigger
        public void setA(String a) {
            this.a = a;
        }

        @MFParmBusTrigger(group = "BC")
        public void setB(String b) {
            this.b = b;
        }

        @MFParmBusTrigger(group = "BC")
        public void setC(String c) {
            this.c = c;
        }

        @MFParmBusTrigger("d")
        public void invokeBusD() {

        }

        public void _setD(String d) {
            this.d = d;
        }

    }

    public static class SampleWritableBean {
        public String a, b, c, d, alias;

        public void setA(String a) {
            this.a = a;
        }

        public void setB(String b) {
            this.b = b;
        }

        public void setC(String c) {
            this.c = c;
        }

        public void setD(String d) {
            this.d = d;
        }

        @MFParmBusAlias("alias")
        public void setD2(String alias) {
            this.alias = alias;
        }

    }

    @Test
    public void testBusTrigger() {
        MFParmInterceptor interceptor = new MFParmInterceptor(new ForTrigger());
        Object proxied = interceptor.getProxied();
        MFParmBusPool busPool = (MFParmBusPool) proxied;
        SampleWritableBean sampleBean = new SampleWritableBean();
        busPool.registerToWeakBus(sampleBean);

        ForTrigger forTrigger = (ForTrigger) proxied;

        forTrigger.setA("expA");
        assertEquals("expA", sampleBean.a);

        forTrigger.setB("expB");
        assertEquals(null, sampleBean.b);
        forTrigger.setC("expC");
        assertEquals("expB", sampleBean.b);
        assertEquals("expC", sampleBean.c);
        forTrigger._setD("expD");
        forTrigger.invokeBusD();
        assertEquals("expD", sampleBean.d);
        assertEquals("expD", sampleBean.alias);
    }
}
