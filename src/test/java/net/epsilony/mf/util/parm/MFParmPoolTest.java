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

import java.util.Arrays;
import java.util.Map;

import net.epsilony.mf.util.parm.MFParmPool.BeanSetterEntry;
import net.epsilony.mf.util.parm.ann.MFParmBusTrigger;
import net.epsilony.mf.util.parm.ann.MFParmLocal;
import net.epsilony.mf.util.parm.ann.MFParmWithBusProxy;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmPoolTest {

    @MFParmWithBusProxy
    public static class BusClass {

        String a = " ";
        String b = " ";
        String c = " ";
        String d = " ";

        @MFParmBusTrigger
        public void setA(String globalA) {
            this.a = globalA;
        }

        @MFParmLocal
        @MFParmBusTrigger(aims = { "b" })
        public void setB(String localB) {
            this.b = localB;
        }

        @MFParmBusTrigger(aims = { "cd" })
        public void setC(String c) {
            this.c = c;
        }

        @MFParmBusTrigger(aims = { "cd" })
        public void setD(String d) {
            this.d = d;
        }

        public String getCd() {
            return c + d;
        }

        public String getA() {
            return a;
        }

        @MFParmLocal
        public String getB() {
            return b;
        }

        @Override
        public String toString() {
            return a + b + c + d;
        }

    }

    public static class Bean {
        String a = " ", b = " ", cd = "  ";

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getCd() {
            return cd;
        }

        public void setCd(String cd) {
            this.cd = cd;
        }

        @Override
        public String toString() {
            return a + b + cd;
        }
    }

    @Test
    public void testSimp() {
        MFParmPool manager = new MFParmPool();
        Bean bean = new Bean();
        BusClass bus = (BusClass) new MFParmInterceptor(new BusClass()).getProxied();

        manager.setBeans(Arrays.asList(bean, bus));

        Map<String, BeanSetterEntry> openEntries = manager.getParmNameToOpenBeanSetterEntry();
        assertEquals(Sets.newHashSet("a", "b", "c", "d"), openEntries.keySet());
        Map<String, MFParmBusProxy> globalBuses = manager.getParmNameToGlobalBusProxy();
        assertEquals(Sets.newHashSet("a", "cd"), globalBuses.keySet());

        bus.setA("a");
        assertEquals("a   ", bean.toString());
        bus.setB("b");
        assertEquals("a   ", bean.toString());
        bus.setC("c");
        assertEquals("a   ", bean.toString());
        bus.setD("d");
        assertEquals("a cd", bean.toString());
    }

    public static class InputBean {
        String a = "a", b = "b", c = "c", d = "d";

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public String getC() {
            return c;
        }

        public String getD() {
            return d;
        }

    }

    @Test
    public void testSimpSetup() {
        MFParmPool manager = new MFParmPool();
        Bean bean = new Bean();
        BusClass bus = (BusClass) new MFParmInterceptor(new BusClass()).getProxied();

        manager.setBeans(Arrays.asList(bean, bus));

        bus.setB("#");
        manager.setup(new InputBean());

        assertEquals("abcd", bean.toString());
        assertEquals("a#cd", bus.toString());
    }

}
