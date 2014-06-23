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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.epsilony.mf.util.parm.MFParmIndex.MFParmDescriptor;
import net.epsilony.mf.util.parm.ann.BusTrigger;
import net.epsilony.mf.util.parm.ann.GlobalBus;
import net.epsilony.mf.util.parm.ann.MFParmName;
import net.epsilony.mf.util.parm.ann.MFParmOptional;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmContainerImplementerTest {

    @Test
    public void test() {
        MFParmContainerImplementor<SampleClass> implementer = new MFParmContainerImplementor<MFParmContainerImplementerTest.SampleClass>(
                SampleClass.class);
        SampleClass proxied = implementer.getProxied();

        TriggerParmToBusSwitcher parmToBusSwitcher = proxied.parmToBusSwitcher();
        MFParmIndex parmIndex = proxied.parmIndex();

        Map<String, MFParmDescriptor> parmDescriptors = parmIndex.getParmDescriptors();
        assertEquals(Sets.newHashSet("a,b,alias,auto".split(",")), parmDescriptors.keySet());

        assertEquals(
                Sets.newHashSet("b"),
                parmDescriptors.keySet().stream().filter(key -> parmDescriptors.get(key).isOptional())
                        .collect(Collectors.toSet()));

        assertEquals(
                Sets.newHashSet("a", "b", "auto"),
                parmDescriptors.keySet().stream().filter(key -> parmDescriptors.get(key).isTrigger())
                        .collect(Collectors.toSet()));

        Set<String> buses = Sets.newHashSet("one,two,three,auto".split(","));
        TriggerParmToBusSwitcher switcher = implementer.getSwitcher();
        assertTrue(switcher == parmToBusSwitcher);

        assertEquals(buses, switcher.getBusNames());

        assertEquals(Sets.newHashSet("three"), switcher.getBusNames().stream().filter(bn -> switcher.isBusGlobal(bn))
                .collect(Collectors.toSet()));

        String[] busTriggers = { "one:a", "two:a,b", "three:b", "auto:a,b,auto" };
        for (String busTrigger : busTriggers) {
            String[] split = busTrigger.split(":");
            String busName = split[0];
            Set<String> triggers = Sets.newHashSet(split[1].split(","));
            assertEquals(triggers, switcher.getBusTriggers(busName));
            assertEquals(triggers, switcher.getBusUninvokedTriggers(busName));
        }

        proxied.setA(" ,x,y,z");
        proxied.setB(" ,X,Y,Z");

        String[] busValues = { "one:1 x X", "two:2 y Y", "three:3 z Z", "auto:1", "auto:2" };
        for (String busValue : busValues) {
            String[] split = busValue.split(":");
            String busName = split[0];
            String value = split[1];
            assertEquals(value, switcher.getBusValueSource(busName).get());
        }
    }

    public static class SampleClass implements MFParmContainer {

        private String[] as;
        private String[] bs;
        public int       auto = 1;

        @BusTrigger(aims = { "one", "two", "auto" })
        public void setA(String a) {
            as = a.split(",");
        }

        @MFParmOptional
        @BusTrigger(aims = { "two", "three", "auto" })
        public void setB(String b) {
            bs = b.split(",");
        }

        @MFParmName("alias")
        public void setC(String c) {

        }

        @BusTrigger
        @MFParmName("auto")
        public void setD(String d) {

        }

        private String get(int index) {
            String a = index < getAs().length ? getAs()[index] : "null";
            String b = index < getBs().length ? getBs()[index] : "null";
            return index + " " + a + " " + b;
        }

        public String getOne() {
            return get(1);
        }

        public String getTwo() {
            return get(2);
        }

        @GlobalBus
        public String getThree() {
            return get(3);
        }

        public String getAuto() {
            return Integer.toString(auto++);
        }

        public int getAny() {
            return 0;
        }

        private String[] getAs() {
            return as;
        }

        private String[] getBs() {
            return bs;
        }
    }

}
