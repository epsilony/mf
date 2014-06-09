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
package net.epsilony.mf.util.proxy.parm;

import java.beans.PropertyDescriptor;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.epsilony.mf.util.MFBeanUtils;
import net.epsilony.mf.util.proxy.parm.ann.MFParmIgnore;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmProtoInterceptor<T> implements MethodInterceptor {

    private final Class<T>               cls;
    private final T                      parmProxy;
    private final List<WeakReference<T>> protos                 = new LinkedList<>();
    private final Set<Method>            parameterSetterMethods = new HashSet<>();
    private final Constructor<T>         constructor;

    public MFParmProtoInterceptor(Class<T> cls) {
        this.cls = cls;

        try {
            constructor = cls.getConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("input class must have a null constructor");
        }

        Stream<PropertyDescriptor> stream = MFBeanUtils.writablePropertyDescriptorStream(cls, true);
        stream.forEach(descriptor -> {
            Method writeMethod = descriptor.getWriteMethod();
            if (writeMethod.isAnnotationPresent(MFParmIgnore.class)) {
                return;
            }
            parameterSetterMethods.add(writeMethod);
        });

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);
        @SuppressWarnings("unchecked")
        T instance = (T) enhancer.create();
        parmProxy = instance;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (!isSetterMethod(method)) {
            throw new IllegalStateException("proxy instance should only be used to set parameters [wrong method:"
                    + method + "]");
        }

        Iterator<WeakReference<T>> iterator = protos.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> ref = iterator.next();
            T proto = ref.get();
            if (null == proto) {
                iterator.remove();
                continue;
            }
            method.invoke(proto, args);
        }

        return null;

    }

    public T getParmProxy() {
        return parmProxy;
    }

    public T newProto() {
        T proto = null;
        try {
            proto = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        protos.add(new WeakReference<>(proto));
        return proto;

    }

    public Class<T> getCls() {
        return cls;
    }

    private boolean isSetterMethod(Method method) {
        return parameterSetterMethods.contains(method);
    }
}
