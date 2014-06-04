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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import net.epsilony.mf.util.MFBeanUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeakBusesProxyInterceptor<T> implements MethodInterceptor {
    public static Logger                       logger     = LoggerFactory.getLogger(WeakBusesProxyInterceptor.class);
    private final Map<String, WeakBus<Object>> weakBusMap = new HashMap<>();
    private final Class<T>                     cls;
    private final T                            proxyShell;
    private final String                       preName;

    public WeakBusesProxyInterceptor(Class<T> cls, String preName) {

        this.preName = preName;
        this.cls = cls;

        Stream<PropertyDescriptor> stream = MFBeanUtils.writablePropertyDescriptorStream(cls, true);
        stream.forEach(descriptor -> {
            String name = descriptor.getName();
            weakBusMap.put(name, new WeakBus<>(this.preName + ": " + cls.getSimpleName() + "." + name));
        });

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);
        @SuppressWarnings("unchecked")
        T instance = (T) enhancer.create();
        this.proxyShell = instance;

    }

    public void register(T normalInstance) {
        if (!cls.isInstance(normalInstance)) {
            throw new IllegalArgumentException();
        }
        Stream<PropertyDescriptor> stream = MFBeanUtils.writablePropertyDescriptorStream(cls, true);
        stream.forEach(descriptor -> {
            String name = descriptor.getName();
            WeakBus<Object> weakBus = weakBusMap.get(name);
            weakBus.register((obj, value) -> {
                try {
                    descriptor.getWriteMethod().invoke(obj, value);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }, normalInstance);
        });
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String propertyNameOfSetter = getPropertyNameOfSetter(method, args);
        if (null == propertyNameOfSetter) {
            logger.warn("method {} ignored, not an property setter", method.getName());
            return null;
        }
        WeakBus<Object> weakBus = weakBusMap.get(propertyNameOfSetter);
        weakBus.post(args[0]);
        return null;
    }

    public T proxyShell() {
        return proxyShell;
    }

    private static String getPropertyNameOfSetter(Method method, Object[] args) {
        String name = method.getName();
        if (name.startsWith("set") && args.length == 1 && method.getReturnType() == Void.TYPE
                && !Modifier.isAbstract(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
            char[] propName = name.substring("set".length()).toCharArray();
            propName[0] = Character.toLowerCase(propName[0]);
            return new String(propName);
        } else {
            return null;
        }
    }

    public WeakBus<Object> getPropertyBus(String name) {
        return weakBusMap.get(name);
    }

    public String getPreName() {
        return preName;
    }

}