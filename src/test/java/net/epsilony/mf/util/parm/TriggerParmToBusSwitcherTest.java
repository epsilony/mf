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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.epsilony.mf.util.parm.ann.AsSubBus;
import net.epsilony.mf.util.parm.ann.MFParmIgnore;
import net.epsilony.mf.util.parm.ann.MFParmName;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriggerParmToBusSwitcherTest {

    TriggerParmToBusSwitcher sampleSwitcher = new TriggerParmToBusSwitcher();
    ArrayList<String>        recorder       = new ArrayList<>();

    private void add(String name, String... aims) {
        sampleSwitcher.addTriggerParm(name, aims);
    }

    private void addSimpleSource(String... busNames) {
        for (String busName : busNames) {
            sampleSwitcher.setBusValueSource(busName, new ValueSource(busName));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void registerListRecorder(String... busNames) {
        for (String busName : busNames) {
            sampleSwitcher.register(busName, List::add, (List) recorder);
        }
    }

    @Test
    public void testSimpleRegister() {
        prepareSimpleRegister();
        registerListRecorder("1 2 3 4".split(" "));

        for (String p : "a,b,c,d".split(",")) {
            sampleSwitcher.triggerParmAims(p);
        }

        assertArrayEquals("4,1,2,3".split(","), recorder.toArray());

    }

    private void prepareSimpleRegister() {
        add("a", "1 2 3 4".split(" "));
        add("b", "1 2 3".split(" "));
        add("c", "2 3".split(" "));
        add("d", "3");

        addSimpleSource("1 2 3 4".split(" "));
    }

    public static class ValueSource implements Supplier<String> {
        String name;

        @Override
        public String get() {
            return name;
        }

        private ValueSource(String name) {
            this.name = name;
        }
    }

    @Test
    public void testAutoRegister() {
        add("a", "two four".split(" "));
        add("b", "one two three".split(" "));
        add("c", "two three".split(" "));
        add("d", "three");

        String[] busValues = "one:1 two:2 three:3 four:4".split(" ");
        for (String busValue : busValues) {
            String[] split = busValue.split(":");
            String bus = split[0];
            String value = split[1];
            sampleSwitcher.setBusValueSource(bus, () -> value);
        }
        SampleBean sampleBean = new SampleBean();
        SampleBean globalBean = new SampleBean();
        sampleSwitcher.autoRegister(sampleBean);
        sampleSwitcher.setBusGlobal("one", true);
        sampleSwitcher.autoRegister(globalBean, true);

        String[] trigExps = { "a:    :    ", "b:1   :1   ", "c:12  :1   ", "d:123 :1   " };
        for (String trigExp : trigExps) {
            String[] split = trigExp.split(":");
            String parm = split[0];
            String exp = split[1];
            String globalExp = split[2];
            sampleSwitcher.triggerParmAims(parm);
            assertEquals(exp, sampleBean.toString());
            assertEquals(globalExp, globalBean.toString());
        }

    }

    public static class SampleBean {

        public String           one   = " ";
        public Supplier<String> three = () -> " ";
        public String           two   = " ";
        public String           four  = " ";

        public void setOne(String one) {
            this.one = one;

        }

        @MFParmName("two")
        public void setAnything(String two) {
            this.two = two;

        }

        @AsSubBus
        public void setThree(Supplier<String> three) {
            this.three = three;

        }

        @MFParmIgnore
        public void setFour(String four) {
            this.four = four;
        }

        @Override
        public String toString() {
            return one + two + three.get() + four;
        }
    }
}
