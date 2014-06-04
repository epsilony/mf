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

import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
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
    private BeanMap                     hubBeanMap;
    private SampleHub                   hub;
    private MFHubInterceptor<SampleHub> hubInterceptor;

    @Test
    public void testSetter() {

        SampleHub src = new SampleHub();
        src.b = null;
        src.d = 3;
        src.e = 4;
        src.f = 5;

        hub.hubSetter(src);

        assertEquals(Sets.newHashSet(), hubInterceptor.getUnsetProperties(false));
        assertEquals(Sets.newHashSet("c"), hubInterceptor.getUnsetProperties(true));

        assertEquals(src.b, hub.b);
        assertEquals(src.d, hub.d);
        assertEquals(src.e, hub.e);
        assertEquals(src.f, hub.f);

        assertEquals(src, hub.src);

    }

    @Test
    public void testAboutNull() throws Throwable {
        hubBeanMap.put("b", null);
        boolean throwed = false;

        try {
            hub.setC(null);
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
        hubBeanMap.put("a", 1);
        assertEquals(1, (int) hub.a);
        assertTrue(hubInterceptor.getParameterValueRecords().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Before
    public void init() {
        ac = new AnnotationConfigApplicationContext(SampleConfig.class);

        Map<String, Object> beansWithAnnotation = ac.getBeansWithAnnotation(MFHub.class);
        Object hubObj = beansWithAnnotation.values().iterator().next();
        hubBeanMap = new BeanMap(hubObj);

        hub = (SampleHub) hubObj;
        hubInterceptor = (MFHubInterceptor<SampleHub>) ac.getBean("mfHubInterceptor");

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
    }

    @MFHub
    public static class SampleHub {
        public Integer a, b, c, d, e, f;
        public Object  src;

        @MFHubIgnore
        public void setA(Integer a) {
            this.a = a;
        }

        @MFNullPolicy(permit = true)
        public void setB(Integer b) {
            this.b = b;
        }

        @MFHubOptional
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

        @MFHubSetter
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

}
