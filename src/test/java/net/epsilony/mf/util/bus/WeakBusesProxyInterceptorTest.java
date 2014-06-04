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
package net.epsilony.mf.util.bus;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class WeakBusesProxyInterceptorTest {
    public static class SampleClass {
        public static int sumValue = 0;

        public void setValue(int value) {
            sumValue += value;
        }

    }

    @Configuration
    public static class MockConfig {
        @Bean
        public WeakBusesProxyInterceptor<SampleClass> sampleClassInterceptor() {
            return new WeakBusesProxyInterceptor<>(SampleClass.class, "");
        }

        @Bean
        @Scope("prototype")
        public SampleClass sampleClass() {

            SampleClass result = new SampleClass();
            sampleClassInterceptor().register(result);
            return result;

        }
    }

    @Test
    public void test() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(MockConfig.class);
        List<SampleClass> samples = new ArrayList<>();
        samples.add(ac.getBean(SampleClass.class));
        samples.add(ac.getBean(SampleClass.class));
        WeakBusesProxyInterceptor<SampleClass> interceptor = ac.getBean(WeakBusesProxyInterceptor.class);
        interceptor.proxyShell().setValue(1);
        assertEquals(samples.size(), SampleClass.sumValue);
        samples.add(ac.getBean(SampleClass.class));
        assertEquals(samples.size(), SampleClass.sumValue);
    }
}
