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

import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RelayParmContainerBuilderTest {

    @Test
    public void test() {
        MFParmContainer container = new RelayParmContainerBuilder()//
                .addParm("a")//
                .addParm("b", true, false)//
                .addParm("c", false, true)//
                .addParm("d", true)//
                .get();

        assertEquals(Sets.newHashSet("a,b,c,d".split(",")), container.parmIndex().getParms());

        assertEquals(Sets.newHashSet("a,b,c,d".split(",")), container.parmToBusSwitcher().getBusNames());

        assertEquals(
                Sets.newHashSet("b", "d"),
                container.parmIndex().getParmDescriptors().values().stream()
                        .filter(desciptor -> desciptor.isAsSubBus()).map(desciptor -> desciptor.getName())
                        .collect(Collectors.toSet()));

        assertEquals(
                Sets.newHashSet("c"),
                container.parmIndex().getParmDescriptors().values().stream()
                        .filter(descriptor -> descriptor.isOptional()).map(descriptor -> descriptor.getName())
                        .collect(Collectors.toSet()));

        assertEquals(
                Collections.emptySet(),
                container.parmToBusSwitcher().getBusNames().stream()
                        .filter(name -> container.parmToBusSwitcher().isBusGlobal(name)).collect(Collectors.toSet()));

        SampleBean bean = new SampleBean();
        SampleBean bean2 = new SampleBean();
        container.autoRegister(bean);
        container.autoRegister(bean2);

        assertEquals("    ", bean.toString());
        container.setParmValue("a", "a");
        assertEquals("a   ", bean.toString());
        assertEquals("a   ", bean2.toString());

        container.setParmValue("b", new Supplier<String>() {
            int index = 0;

            @Override
            public String get() {
                return "b" + Integer.toString(index++);
            }
        });

        assertEquals("ab0  ", bean.toString());
        assertEquals("ab1  ", bean2.toString());

        container.setParmValue("c", "c");
        assertEquals("ab0c ", bean.toString());
        assertEquals("ab1c ", bean2.toString());

        container.setParmValue("d", new Supplier<String>() {
            int index = 0;

            @Override
            public String get() {
                return "d" + index++;
            }
        });

        assertEquals("ab0cd0", bean.toString());
        assertEquals("ab1cd1", bean2.toString());

    }

    public static class SampleBean {

        String a = " ", b = " ", c = " ", d = " ";

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

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        @Override
        public String toString() {
            return a + b + c + d;
        }

    }
}
