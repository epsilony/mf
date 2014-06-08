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
package net.epsilony.mf.util.proxy.barrier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.google.common.collect.Sets;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFBarrierInterceptor<T> implements MethodInterceptor {

    private Class<T>                                 cls;
    private Enhancer                                 enhancer;
    private WeakHashMap<T, Map<String, Set<Method>>> instancesBarrierStateMap = new WeakHashMap<>();
    private Map<String, Set<Method>>                 barrierGroups;
    private Map<String, Set<Method>>                 invokerGroups;

    public MFBarrierInterceptor(Class<T> cls) {
        this.cls = cls;

        genBarrierGroups();

        genInvokerGroups();

        enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);

    }

    private void genBarrierGroups() {
        barrierGroups = new LinkedHashMap<>();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            MFBarrier barrier = method.getAnnotation(MFBarrier.class);
            if (null == barrier) {
                continue;
            }

            if (!method.getReturnType().equals(void.class) || method.getParameterCount() > 0) {
                throw new IllegalStateException("@" + MFBarrierInvoker.class
                        + " method should be void and without parameter +(" + method + ")");
            }

            String group = barrier.group();
            Set<Method> groupMethods = barrierGroups.get(group);
            if (null == groupMethods) {
                groupMethods = Sets.newHashSet(method);
                barrierGroups.put(group, groupMethods);
            } else {
                groupMethods.add(method);
            }
        }
    }

    private void genInvokerGroups() {
        Method[] methods = cls.getMethods();
        invokerGroups = new HashMap<>();

        for (Method method : methods) {
            MFBarrierInvoker barrierInvoker = method.getAnnotation(MFBarrierInvoker.class);
            if (null == barrierInvoker) {
                continue;
            }

            String group = barrierInvoker.group();
            if (!barrierGroups.containsKey(group)) {
                throw new IllegalStateException(method.getName() + " group [" + group + "] " + "lack of relative @"
                        + MFBarrier.class.getSimpleName() + "support");
            }

            Set<Method> slibingInvokers = invokerGroups.get(group);
            if (null == slibingInvokers) {
                slibingInvokers = new LinkedHashSet<>();
                slibingInvokers.add(method);
                invokerGroups.put(group, slibingInvokers);
            } else {
                slibingInvokers.add(method);
            }
        }

    }

    public T newInstance() {
        @SuppressWarnings("unchecked")
        T result = (T) enhancer.create();
        instancesBarrierStateMap.put(result, genInitBarrierStateMap());
        return result;
    }

    private Map<String, Set<Method>> genInitBarrierStateMap() {
        Map<String, Set<Method>> result = new HashMap<>();
        for (Map.Entry<String, Set<Method>> entry : barrierGroups.entrySet()) {
            result.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return result;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object ret = proxy.invokeSuper(obj, args);

        MFBarrier barrier = method.getAnnotation(MFBarrier.class);
        if (barrier == null) {
            return ret;
        }

        String group = barrier.group();
        Map<String, Set<Method>> stateMap = instancesBarrierStateMap.get(obj);
        if (null == stateMap) {
            return ret;
        }

        Set<Method> groupState = stateMap.get(group);
        if (null == groupState) {
            return ret;
        }

        groupState.remove(method);
        if (!groupState.isEmpty()) {
            return ret;
        }

        stateMap.remove(groupState);

        if (stateMap.isEmpty()) {
            instancesBarrierStateMap.remove(stateMap);
        }

        invokeBarrierInvokers(obj, group);

        return ret;
    }

    private void invokeBarrierInvokers(Object obj, String group) {
        Set<Method> invokers = invokerGroups.get(group);
        for (Method method : invokers) {
            try {
                method.invoke(obj);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
