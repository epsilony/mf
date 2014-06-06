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

import java.beans.PropertyDescriptor;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.parm.MFParmBusPool;
import net.epsilony.mf.util.parm.MFParmBusPoolRegsiter;
import net.epsilony.mf.util.parm.MFParmBusSource;
import net.epsilony.mf.util.parm.MFParmBusTrigger;
import net.epsilony.mf.util.parm.MFParmIgnore;
import net.epsilony.mf.util.parm.MFParmOptional;
import net.epsilony.mf.util.parm.MFParmPackSetter;
import net.epsilony.mf.util.parm.MFParmUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MFHubInterceptor<T> implements MethodInterceptor {

    public static Logger                       logger                 = LoggerFactory.getLogger(MFHubInterceptor.class);

    private final Class<T>                     cls;
    private final T                            proxied;
    private final String                       proxiedName;
    private Map<String, String>                methodNameToParameterName;
    private Map<String, Method>                parameterNameToWriteMethod;
    private Set<String>                        optionalParameters;
    private Map<String, WeakReference<Object>> parameterValueRecords  = new HashMap<>();
    private Map<String, WeakReference<Object>> parameterSourceRecords = new HashMap<>();
    private final Method                       hubPackSetterMethod;
    private final boolean                      defaultNullPolicy;
    private Map<String, WeakBus<Object>>       parameterNameToWeakBus = new HashMap<>();
    private final Method                       busPoolMethod;
    private final Method                       busPoolRegistryMethod;
    private Object                             currentSource;

    public MFHubInterceptor(Class<T> cls) {
        this.cls = cls;
        MFHub mfHub = cls.getAnnotation(MFHub.class);
        if (null == mfHub) {
            throw new IllegalArgumentException("target object class is not annotated by @"
                    + MFHub.class.getSimpleName());
        }

        defaultNullPolicy = MFParmUtils.isClassNullPermit(cls);

        initByDescriptors();

        hubPackSetterMethod = getHubPackSetterMethod();

        busPoolMethod = buildBusPool();

        busPoolRegistryMethod = getBusPoolRegistryMethod();

        checkBusTriggers();

        proxied = generateProxied();

        proxiedName = cls.getSimpleName() + "@" + proxied.hashCode();

    }

    public void initByDescriptors() {
        methodNameToParameterName = new HashMap<>();
        parameterNameToWriteMethod = new HashMap<>();
        optionalParameters = new HashSet<>();

        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(cls);
        for (PropertyDescriptor descriptor : descriptors) {
            Method writeMethod = descriptor.getWriteMethod();
            if (null == writeMethod) {
                continue;
            }
            if (null != writeMethod.getAnnotation(MFParmIgnore.class)) {
                continue;
            }
            String parameter = descriptor.getName();
            parameterNameToWriteMethod.put(parameter, writeMethod);
            methodNameToParameterName.put(writeMethod.getName(), parameter);

            if (null != writeMethod.getAnnotation(MFParmOptional.class)) {
                optionalParameters.add(parameter);
            }
        }

    }

    private Method getHubPackSetterMethod() {
        Method[] methods = cls.getMethods();
        Method _hubSetterMethod = null;
        for (Method method : methods) {
            if (method.getParameterCount() == 1 && method.getAnnotation(MFParmPackSetter.class) != null) {
                if (_hubSetterMethod != null) {
                    throw new IllegalArgumentException("MFHubSetter annotation is not unique");
                } else {
                    if (methodNameToParameterName.containsKey(method.getName())) {
                        throw new IllegalArgumentException("MFHubSetter cannot be annotated to parameter setter ("
                                + method.getName() + ")!");
                    }
                    _hubSetterMethod = method;
                }
            }
        }
        return _hubSetterMethod;
    }

    private Method buildBusPool() {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null || !readMethod.isAnnotationPresent(MFParmBusSource.class)) {
                continue;
            }

            String parameterName = descriptor.getName();
            WeakBus<Object> bus = new WeakBus<>(cls.getSimpleName() + ": " + parameterName);
            parameterNameToWeakBus.put(parameterName, bus);
        }

        Method result = getBusPoolMethod();
        if (result == null && !parameterNameToWeakBus.isEmpty()) {
            throw new IllegalArgumentException("missing @" + MFParmBusSource.class.getSimpleName());
        }
        return result;
    }

    private Method getBusPoolMethod() {
        Method[] methods = cls.getMethods();
        Method result = null;
        for (Method method : methods) {
            if (!method.isAnnotationPresent(MFParmBusPool.class)) {
                continue;
            }
            boolean isRightMethodType = MFParmUtils.isMethodFitBusPool(method);
            if (!isRightMethodType) {
                throw new IllegalArgumentException("wrong @" + MFParmBusPool.class.getSimpleName()
                        + " target method type");
            }

            if (result != null) {
                throw new IllegalArgumentException("@" + MFParmBusPool.class.getSimpleName() + " is not unique");
            }

            result = method;

        }
        return result;
    }

    private Method getBusPoolRegistryMethod() {
        Method[] methods = cls.getMethods();
        Method result = null;
        for (Method method : methods) {
            if (!method.isAnnotationPresent(MFParmBusPoolRegsiter.class)) {
                continue;
            }
            boolean isMethodFitBusPoolRegister = method.getParameterCount() == 1
                    && method.getParameters()[0].getType().isAssignableFrom(Object.class);
            if (!isMethodFitBusPoolRegister) {
                throw new IllegalStateException("method [" + method + " is not fit for @"
                        + MFParmBusPoolRegsiter.class.getSimpleName());
            }
            if (result != null) {
                throw new IllegalStateException("@" + MFParmBusPoolRegsiter.class.getSimpleName() + " is not unique");
            }
            result = method;
        }
        return result;
    }

    private void checkBusTriggers() {
        Map<Method, List<String>> missings = new HashMap<>();
        for (Method method : cls.getMethods()) {
            if (!isBusTriggerMethod(method)) {
                continue;
            }
            String[] triggerMethodAims = getTriggerMethodAims(method);
            List<String> missing = new ArrayList<>();
            for (String busParameter : triggerMethodAims) {
                if (!parameterNameToWeakBus.containsKey(busParameter)) {
                    missing.add(busParameter);
                }
            }
            if (!missing.isEmpty()) {
                missings.put(method, missing);
            }
        }

        if (missings.isEmpty()) {
            return;
        }

        StringBuilder exceptionMessge = new StringBuilder();
        exceptionMessge.append("missing @" + MFParmBusSource.class.getSimpleName() + "annotated bus source: ");

        for (Map.Entry<Method, List<String>> entry : missings.entrySet()) {
            Method method = entry.getKey();
            List<String> miss = entry.getValue();
            exceptionMessge.append("[trigger " + method + " missing: " + miss + "];");
        }

        throw new IllegalStateException(exceptionMessge.toString());
    }

    public T generateProxied() {
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

    public Set<String> getOptionalParameters() {
        return Collections.unmodifiableSet(optionalParameters);
    }

    public Class<T> getTargetClass() {
        return cls;
    }

    public T getProxied() {
        return proxied;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (isHubSetter(method)) {
            @SuppressWarnings("unchecked")
            T target = (T) obj;
            setupTarget(args[0], target);
            if (Modifier.isAbstract(method.getModifiers())) {
                return null;
            } else {
                return proxy.invokeSuper(obj, args);
            }
        }

        if (isBusPoolMethod(method)) {
            String parameterName = (String) args[0];
            if (!Modifier.isAbstract(method.getModifiers())) {
                Object ret = proxy.invokeSuper(obj, args);
                if (null != ret) {
                    logger.warn(obj + " " + method + " should not have content!");
                }
            }
            WeakBus<Object> weakBus = parameterNameToWeakBus.get(parameterName);
            return weakBus;
        }

        if (isBusPoolRegister(method)) {
            if (!Modifier.isAbstract(method.getModifiers())) {
                Object ret = proxy.invokeSuper(obj, args);
                if (null != ret) {
                    logger.warn(obj + " " + method + " should not have content!");
                }
            }
            Map<String, Method> findParameterNameToSetter = MFParmUtils.findParameterNameToSetter(args[0].getClass());
            for (Map.Entry<String, Method> entry : findParameterNameToSetter.entrySet()) {
                String parameterName = entry.getKey();

                WeakBus<Object> weakBus = parameterNameToWeakBus.get(parameterName);
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

        Object ret = null;
        if (isParameterSetter(method)) {
            checkNull(args[0], method);
            ret = proxy.invokeSuper(obj, args);
            recordParameterSetup(args[0], method);
        } else if (!Modifier.isAbstract(method.getModifiers())) {
            ret = proxy.invokeSuper(obj, args);
        }

        if (isBusTriggerMethod(method)) {
            String[] aims = getTriggerMethodAims(method);
            BeanMap beanMap = new BeanMap(obj);
            for (String parameterName : aims) {
                WeakBus<Object> weakBus = parameterNameToWeakBus.get(parameterName);
                weakBus.postToEach(() -> beanMap.get(parameterName));
            }
        }

        return ret;
    }

    private boolean isBusPoolRegister(Method method) {
        return method.equals(busPoolRegistryMethod);
    }

    private String[] getTriggerMethodAims(Method method) {
        String[] value = method.getAnnotation(MFParmBusTrigger.class).value();
        if (value.length == 0) {
            String parameterName = methodToParameterName(method);
            if (null == parameterName) {
                throw new IllegalArgumentException("@" + MFParmBusTrigger.class.getSimpleName()
                        + ".value must be set when the annotation aim is not a bean setter (" + method + ")");
            }
            value = new String[] { parameterName };
        }
        return value;
    }

    private boolean isBusTriggerMethod(Method method) {
        return method.isAnnotationPresent(MFParmBusTrigger.class);
    }

    private boolean isBusPoolMethod(Method method) {
        return method.equals(busPoolMethod);
    }

    private void setupTarget(Object src, T target) {
        logger.info("start setting {} -> {}", src, target);

        currentSource = src;
        BeanMap srcMap = new BeanMap(src);
        BeanMap destMap = new BeanMap(target);
        for (Map.Entry<String, Method> entry : parameterNameToWriteMethod.entrySet()) {
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
        String parameter = methodToParameterName(writeMethod);
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

    private void checkNull(Object value, Method writeMethod) {
        String parameter = methodToParameterName(writeMethod);
        if (null == value) {
            boolean permitNull = MFParmUtils.isMethodNullPermit(defaultNullPolicy, writeMethod);
            if (!permitNull) {
                throw new NullPointerException("null value is not permitted" + parameter);
            }
        }
    }

    private boolean isHubSetter(Method method) {
        return method.equals(hubPackSetterMethod);
    }

    private String methodToParameterName(Method method) {
        return methodNameToParameterName.get(method.getName());
    }

    private boolean isParameterSetter(Method method) {
        return methodNameToParameterName.containsKey(method.getName());
    }

    public Set<String> getUnsetProperties(boolean countOptional) {
        Set<String> result = new HashSet<>(parameterNameToWriteMethod.keySet());
        result.removeAll(parameterValueRecords.keySet());
        if (!countOptional) {
            result.removeAll(optionalParameters);
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
}
