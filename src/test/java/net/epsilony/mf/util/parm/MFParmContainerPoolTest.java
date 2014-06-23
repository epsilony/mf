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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.epsilony.mf.util.parm.ann.AsSubBus;
import net.epsilony.mf.util.parm.ann.BusTrigger;
import net.epsilony.mf.util.parm.ann.GlobalBus;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmContainerPoolTest {

    @Test
    public void test() {
        MFParmContainerPool pool = new MFParmContainerPool();
        B beanB = new MFParmContainerImplementor<>(B.class).getProxied();
        pool.addParmContainer(beanB);
        C beanC = new MFParmContainerImplementor<>(C.class).getProxied();
        pool.addParmContainer(beanC);
        A beanA = new MFParmContainerImplementor<>(A.class).getProxied();
        pool.addParmContainer(beanA);

        pool.prepare();

        Map<String, MFParmContainer> globalBusContainers = pool.getGlobalBusContainers();
        assertEquals(Sets.newHashSet("a,b,c,e,f".split(",")), globalBusContainers.keySet());

        Map<String, List<MFParmContainer>> openParmContainers = pool.getOpenParmContainers();
        assertEquals(Sets.newHashSet("a,b,d,e".split(",")), openParmContainers.keySet());

        for (String pv : "a:a:Aa   -Ba   -C ,b:bc:Aabc -Bab  -C ,d:d:Aabcd-Bab  -C ,e:ef:Aabcd-Babef-Ce".split(",")) {
            String[] split = pv.split(":");
            String parm = split[0];
            String value = split[1];
            pool.setOpenParm(parm, value);

            String beansExps = split[2];
            String[] beansExpsSplit = beansExps.split("-");
            String expA = beansExpsSplit[0];
            String expB = beansExpsSplit[1];
            String expC = beansExpsSplit[2];

            assertEquals(expA, beanA.toString());
            assertEquals(expB, beanB.toString());
            assertEquals(expC, beanC.toString());
        }

    }

    public static class A implements MFParmContainer {

        private String a = " ", b = " ", c = " ", d = " ";

        @GlobalBus
        public String getA() {
            return a;
        }

        @BusTrigger
        public void setA(String a) {
            this.a = a;
        }

        @GlobalBus
        public String getB() {
            return b;
        }

        @BusTrigger(aims = { "b", "c" })
        public void setB(String bc) {
            String[] splits = bc.split("");
            this.b = splits[0];
            this.c = splits[1];
        }

        @GlobalBus
        public String getC() {
            return c;
        }

        public String getD() {
            return d;
        }

        @BusTrigger
        public void setD(String d) {
            this.d = d;
        }

        @Override
        public String toString() {
            return "A" + a + b + c + d;
        }

    }

    public static class B implements MFParmContainer {
        private String           a = " ", e = " ", f = " ";
        private Supplier<String> bs;

        @GlobalBus
        public String getE() {
            return e;
        }

        @BusTrigger(aims = { "e", "f" })
        public void setE(String ef) {
            String[] splits = ef.split("");
            e = splits[0];
            f = splits[1];
        }

        @GlobalBus
        public String getF() {
            return f;
        }

        @BusTrigger
        public void setA(String a) {
            this.a = a;
        }

        @AsSubBus
        @BusTrigger
        public void setB(Supplier<String> bs) {
            this.bs = bs;
        }

        public String getA() {
            return a;
        }

        public String getB() {
            if (bs == null) {
                return " ";
            }
            return bs.get();
        }

        @Override
        public String toString() {
            return "B" + a + getB() + e + f;
        }

    }

    public static class C implements MFParmContainer {
        private String e = " ";

        public String getE() {
            return e;
        }

        public void setE(String e) {
            this.e = e;
        }

        @Override
        public String toString() {
            return "C" + e;
        }
    }

}
