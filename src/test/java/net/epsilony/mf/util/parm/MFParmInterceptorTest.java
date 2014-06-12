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

import java.util.function.Supplier;

import net.epsilony.mf.util.parm.ann.MFParmAsSubBus;
import net.epsilony.mf.util.parm.ann.MFParmBusTrigger;
import net.epsilony.mf.util.parm.ann.MFParmName;
import net.epsilony.mf.util.parm.ann.MFParmWithBusProxy;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmInterceptorTest {

    @MFParmWithBusProxy
    public static class ForTrigger {

        String a = " ", b = " ", c = " ", d = " ", e = "e";

        public String getA() {
            return a;
        }

        @MFParmName("alias")
        public String getD() {
            return d;
        }

        public String getE() {
            return e;
        }

        @MFParmBusTrigger
        public void setA(String a) {
            this.a = a;
        }

        @MFParmBusTrigger(aims = { "alias", "e" })
        public void setB(String b) {
            this.b = b;
        }

        @MFParmBusTrigger(aims = { "alias" })
        public void setC(String c) {
            this.c = c;
        }

        public void setD(String d) {
            this.d = d;
        }

        @Override
        public String toString() {
            return a + b + c + d;
        }

    }

    public static class SampleWritableBean {
        public String a = " ", b = " ", c = " ", d = " ", e = " ";

        public void setA(String a) {
            this.a = a;
        }

        public void setB(String b) {
            this.b = b;
        }

        public void setC(String c) {
            this.c = c;
        }

        @MFParmName("alias")
        public void setD(String alias) {
            this.d = alias;
        }

        public void setE(String e) {
            this.e = e;
        }

        @Override
        public String toString() {
            return a + b + c + d + e;
        }

    }

    @Test
    public void testBusTrigger() {
        MFParmInterceptor interceptor = new MFParmInterceptor(new ForTrigger());
        Object proxied = interceptor.getProxied();
        MFParmBusProxy busProxy = (MFParmBusProxy) proxied;
        SampleWritableBean sampleBean = new SampleWritableBean();
        busProxy.registerToWeakBuses(sampleBean);

        ForTrigger forTrigger = (ForTrigger) proxied;
        forTrigger.setD("d");

        forTrigger.setA("a");
        assertEquals("a    ", sampleBean.toString());
        forTrigger.setB("b");
        assertEquals("a   e", sampleBean.toString());
        forTrigger.setC("c");
        assertEquals("a  de", sampleBean.toString());

        assertTrue(busProxy.getParmNameToBusEntry().get("a").isGlobal());
    }

    @MFParmWithBusProxy
    public static class Sample2 {

        public int index = 0;

        @MFParmBusTrigger(aims = { "index" })
        public void setInvoke(Object none) {

        }

        public int getIndex() {
            return index++;
        }
    }

    public static class SampleBean2 {

        public Supplier<Integer> supplier;

        @MFParmAsSubBus
        @MFParmName("index")
        public void setIndexSupplier(Supplier<Integer> supplier) {
            this.supplier = supplier;
        }

    }

    @Test
    public void testRegisterAsSubBus() {
        MFParmInterceptor interceptor = new MFParmInterceptor(new Sample2());
        Object proxied = interceptor.getProxied();
        MFParmBusProxy busProxy = (MFParmBusProxy) proxied;
        SampleBean2 beanA = new SampleBean2();
        SampleBean2 beanB = new SampleBean2();
        busProxy.registerToWeakBuses(beanA);
        busProxy.registerToWeakBuses(beanB);

        Sample2 sample2 = (Sample2) proxied;
        sample2.setInvoke(null);

        assertEquals(beanA.supplier, beanB.supplier);
        assertEquals(0, (int) beanA.supplier.get());
        assertEquals(1, (int) beanA.supplier.get());
        assertEquals(2, (int) beanB.supplier.get());
    }
}
