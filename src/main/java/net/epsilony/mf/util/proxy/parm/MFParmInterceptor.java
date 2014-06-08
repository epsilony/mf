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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.bus.WeakBus;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MFParmInterceptor<T> implements MethodInterceptor {

    public static Logger                             logger                 = LoggerFactory
                                                                                    .getLogger(MFParmInterceptor.class);

    private final Class<T>                           cls;
    private final MFParmIntrospector<T>              parmIntrospector;
    private final T                                  proxied;
    private final String                             proxiedName;

    private final Map<String, WeakReference<Object>> parameterValueRecords  = new HashMap<>();
    private final Map<String, WeakReference<Object>> parameterSourceRecords = new HashMap<>();

    private Object                                   currentSource;

    private final Map<Method, MethodInterceptor>     methodInterceptorMap   = new HashMap<>();

    public MFParmInterceptor(Class<T> cls) {
        this.cls = cls;

        parmIntrospector = new MFParmIntrospector<>(cls);

        proxied = generateProxied();

        proxiedName = cls.getSimpleName() + "@" + proxied.hashCode();

        buildMethodInterceptorMap();

    }

    private T generateProxied() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);
        @SuppressWarnings("unchecked")
        T _proxied = (T) enhancer.create();
        return _proxied;
    }

    public Map<String, WeakReference<Object>> getParameterValueRecords() {
        return Collections.unmodifiableMap(parameterValueRecords);
    }

    public Map<String, WeakReference<Object>> getParameterSourceRecords() {
        return Collections.unmodifiableMap(parameterSourceRecords);
    }

    public Class<T> getTargetClass() {
        return cls;
    }

    public T getProxied() {
        return proxied;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        MethodInterceptor methodInterceptor = methodInterceptorMap.get(method);
        if (null != methodInterceptor) {
            return methodInterceptor.intercept(obj, method, args, proxy);
        }

        Object ret = null;
        if (isParameterSetter(method)) {
            checkNull(args[0], method);
            ret = proxy.invokeSuper(obj, args);
            recordParameterSetup(args[0], method);
        } else if (!Modifier.isAbstract(method.getModifiers())) {
            ret = proxy.invokeSuper(obj, args);
        }

        if (isBusTriggerMethod(method)) {
            String[] aims = parmIntrospector.getBusTriggerMethodAims(method);
            BeanMap beanMap = new BeanMap(obj);
            for (String parameterName : aims) {
                WeakBus<Object> weakBus = getWeakBus(parameterName);
                weakBus.postToEach(() -> beanMap.get(parameterName));
            }
        }

        return ret;
    }

    private void buildMethodInterceptorMap() {
        if (null != parmIntrospector.getPackSetter()) {
            methodInterceptorMap.put(parmIntrospector.getPackSetter(), new MethodInterceptor() {

                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    @SuppressWarnings("unchecked")
                    T target = (T) obj;
                    setupTarget(args[0], target);
                    if (Modifier.isAbstract(method.getModifiers())) {
                        return null;
                    } else {
                        return proxy.invokeSuper(obj, args);
                    }
                }
            });
        }

        if (null != parmIntrospector.getBusPoolMethod()) {
            methodInterceptorMap.put(parmIntrospector.getBusPoolMethod(), new MethodInterceptor() {

                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    String parameterName = (String) args[0];
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        Object ret = proxy.invokeSuper(obj, args);
                        if (null != ret) {
                            logger.warn(obj + " " + method + " should not have content!");
                        }
                    }
                    WeakBus<Object> weakBus = getWeakBus(parameterName);
                    return weakBus;
                }
            });
        }

        if (null != parmIntrospector.getBusPoolRegistryMethod()) {
            methodInterceptorMap.put(parmIntrospector.getBusPoolRegistryMethod(), new MethodInterceptor() {

                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        Object ret = proxy.invokeSuper(obj, args);
                        if (null != ret) {
                            logger.warn(obj + " " + method + " should not have content!");
                        }
                    }
                    Map<String, Method> findParameterNameToSetter = MFParmUtils.searchParameterNameToSetter(args[0]
                            .getClass());
                    for (Map.Entry<String, Method> entry : findParameterNameToSetter.entrySet()) {
                        String parameterName = entry.getKey();

                        WeakBus<Object> weakBus = getWeakBus(parameterName);
                        if (null == weakBus) {
                            continue;
                        }
                        Method WriteMethod = entry.getValue();
                        weakBus.register((consumer, value) -> {
                            try {
                                WriteMethod.invoke(consumer, value);
                            } catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                        }, args[0]);
                        logger.info("register : {}->{}.{}", cls.getSimpleName(), args[0], parameterName);
                    }
                    return null;
                }
            });
        }
    }

    private boolean isBusTriggerMethod(Method method) {
        return method.isAnnotationPresent(MFParmBusTrigger.class);
    }

    private void setupTarget(Object src, T target) {
        logger.info("start setting {} -> {}", src, target);

        currentSource = src;
        BeanMap srcMap = new BeanMap(src);
        BeanMap destMap = new BeanMap(target);
        for (Map.Entry<String, Method> entry : parameterNameToWriteMethod().entrySet()) {
            String parameter = entry.getKey();
            if (!srcMap.containsKey(parameter)) {
                continue;
            }

            Object value = srcMap.get(parameter);
            destMap.put(parameter, value);
        }
        currentSource = null;
        logger.info("finished {} -> {}", src, target);
    }

    private void recordParameterSetup(Object value, Method writeMethod) {
        String parameter = parameterName(writeMethod);
        recordParameterSetup(value, parameter);
    }

    private void recordParameterSetup(Object value, String parameter) {
        if (null != value) {
            parameterValueRecords.put(parameter, new WeakReference<Object>(value));
        } else {
            parameterValueRecords.put(parameter, null);
        }

        parameterSourceRecords.put(parameter, new WeakReference<Object>(currentSource));
        logger.info("{} recieved {}: {}", proxiedName, parameter, value);
    }

    public Set<String> getUnsetProperties(boolean countOptional) {
        Set<String> result = new HashSet<>(parameterNameToWriteMethod().keySet());
        result.removeAll(parameterValueRecords.keySet());
        if (!countOptional) {
            result.removeAll(parmIntrospector.getOptionalParameters());
        }
        return result;
    }

    public Set<String> getSetToNullProperties() {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, WeakReference<Object>> entry : parameterValueRecords.entrySet()) {
            if (entry.getValue() == null) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private void checkNull(Object value, Method writeMethod) {
        if (null == value) {
            boolean permitNull = parmIntrospector.isNullPermitted(writeMethod);
            if (!permitNull) {
                throw new NullPointerException("null value is not permitted" + writeMethod);
            }
        }
    }

    private String parameterName(Method method) {
        return parmIntrospector.getParameterName(method);
    }

    private Map<String, Method> parameterNameToWriteMethod() {
        return parmIntrospector.getParameterNameToWriteMethod();
    }

    public Set<String> getOptionalParameters() {
        return parmIntrospector.getOptionalParameters();
    }

    public WeakBus<Object> getWeakBus(String parameterName) {
        return parmIntrospector.getWeakBus(parameterName);
    }

    public Method getBusPoolMethod() {
        return parmIntrospector.getBusPoolMethod();
    }

    public Method getBusPoolRegistryMethod() {
        return parmIntrospector.getBusPoolRegistryMethod();
    }

    public boolean isParameterSetter(Method method) {
        return parmIntrospector.isParameterSetter(method);
    }

}
