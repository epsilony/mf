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
package net.epsilony.mf.util.hub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.parm.MFParmBusPool;
import net.epsilony.mf.util.parm.MFParmBusSource;
import net.epsilony.mf.util.parm.MFParmBusTrigger;
import net.epsilony.mf.util.parm.MFParmIgnore;
import net.epsilony.mf.util.parm.MFParmNullPolicy;
import net.epsilony.mf.util.parm.MFParmOptional;
import net.epsilony.mf.util.parm.MFParmPackSetter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFHubInterceptorTest {

    private ApplicationContext          ac;
    private SampleHub                   sampleHub;
    private MFHubInterceptor<SampleHub> hubInterceptor;
    private SampleWithBusHub            withBusHub;

    @Test
    public void testSetter() {

        SampleHub src = new SampleHub();
        src.b = null;
        src.d = 3;
        src.e = 4;
        src.f = 5;

        sampleHub.hubSetter(src);

        assertEquals(Sets.newHashSet(), hubInterceptor.getUnsetProperties(false));
        assertEquals(Sets.newHashSet("c"), hubInterceptor.getUnsetProperties(true));

        assertEquals(src.b, sampleHub.b);
        assertEquals(src.d, sampleHub.d);
        assertEquals(src.e, sampleHub.e);
        assertEquals(src.f, sampleHub.f);

        assertEquals(src, sampleHub.src);

    }

    @Test
    public void testAboutNull() throws Throwable {
        sampleHub.setB(null);

        boolean throwed = false;

        try {
            sampleHub.setC(null);
        } catch (Throwable e) {
            if (e instanceof NullPointerException) {
                throwed = true;
            } else {
                throw e;
            }
        }
        assertTrue(throwed);

        assertEquals(Sets.newHashSet("b"), hubInterceptor.getSetToNullProperties());

        assertEquals(Sets.newHashSet("cdef".split("")), hubInterceptor.getUnsetProperties(true));
        assertEquals(Sets.newHashSet("def".split("")), hubInterceptor.getUnsetProperties(false));
    }

    @Test
    public void testIgnored() {
        sampleHub.setA(1);
        assertEquals(1, (int) sampleHub.a);
        assertTrue(hubInterceptor.getParameterValueRecords().isEmpty());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWithBus() {
        WeakBus<Object> aBus = withBusHub.busPool("a");
        WeakBus<Object> bBus = withBusHub.busPool("b");
        WeakBus<Object> cBus = withBusHub.busPool("c");

        assertTrue(aBus != null);
        assertTrue(bBus != null);
        assertTrue(cBus == null);

        List<StringConsumer> acs = Arrays.asList(new StringConsumer(), new StringConsumer());
        for (StringConsumer a : acs) {
            aBus.register(Consumer::accept, (Consumer) a);
        }
        List<StringConsumer> bcs = Arrays.asList(new StringConsumer(), new StringConsumer());
        for (StringConsumer b : bcs) {
            bBus.register(Consumer::accept, (Consumer) b);
        }

        withBusHub.setA("expA");
        for (StringConsumer a : acs) {
            assertEquals("expA", a.value);
        }

        StringConsumer d = new StringConsumer();
        withBusHub.busPool("d").register((con, obj) -> {
            String value = (String) obj;
            d.accept(value);
        }, d);
        withBusHub.trigger("expB expD");
        for (int i = 0; i < bcs.size(); i++) {
            StringConsumer b = bcs.get(i);
            assertEquals("expB" + i, b.value);
        }
        assertEquals("expD", d.value);
    }

    public static class StringConsumer implements Consumer<String> {
        public String value;

        @Override
        public void accept(String value) {
            this.value = value;
        }

    }

    @SuppressWarnings("unchecked")
    @Before
    public void init() {
        ac = new AnnotationConfigApplicationContext(SampleConfig.class);

        sampleHub = ac.getBean(SampleHub.class);

        hubInterceptor = (MFHubInterceptor<SampleHub>) ac.getBean("mfHubInterceptor");

        withBusHub = ac.getBean(SampleWithBusHub.class);
    }

    @Configuration
    public static class SampleConfig {
        @Bean
        public SampleHub sampleHub() {
            return mfHubInterceptor().getProxied();
        }

        @Bean
        public MFHubInterceptor<SampleHub> mfHubInterceptor() {
            return new MFHubInterceptor<MFHubInterceptorTest.SampleHub>(SampleHub.class);
        }

        @Bean
        public SampleWithBusHub sampleWithBusHub() {
            return withBusInterceptor().getProxied();
        }

        @Bean
        public MFHubInterceptor<SampleWithBusHub> withBusInterceptor() {
            return new MFHubInterceptor<>(SampleWithBusHub.class);
        }
    }

    @MFHub
    public static class SampleHub {
        public Integer a, b, c, d, e, f;
        public Object  src;

        @MFParmIgnore
        public void setA(Integer a) {
            this.a = a;
        }

        @MFParmNullPolicy(permit = true)
        public void setB(Integer b) {
            this.b = b;
        }

        @MFParmOptional
        public void setC(Integer c) {
            this.c = c;
        }

        public void setD(Integer d) {
            this.d = d;
        }

        public void setE(Integer e) {
            this.e = e;
        }

        public void setF(Integer f) {
            this.f = f;
        }

        @MFParmPackSetter
        public void hubSetter(Object src) {
            this.src = src;
        }

        public Integer getA() {
            return a;
        }

        public Integer getB() {
            return b;
        }

        //
        // public Integer getC() {
        // return c;
        // }

        public Integer getD() {
            return d;
        }

        public Integer getE() {
            return e;
        }

        public Integer getF() {
            return f;
        }

    }

    @MFHub
    public static abstract class SampleWithBusHub {

        @MFParmBusPool
        public abstract WeakBus<Object> busPool(String parameterName);

        @MFParmBusTrigger
        public void setA(String a) {
            this.a = a;
        }

        @MFParmBusSource
        public String getA() {
            return a;
        }

        String a, b, d;

        int    time = 0;

        @MFParmBusSource
        public String getB() {
            return b + time++;
        }

        public void setC(String c) {

        }

        @MFParmBusSource
        public String getD() {
            return d;
        }

        @MFParmBusTrigger({ "b", "d" })
        public void trigger(String value) {
            String[] split = value.split(" ");
            b = split[0];
            d = split[1];
        }
    }

}
