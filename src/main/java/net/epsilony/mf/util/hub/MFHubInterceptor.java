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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.parm.MFParmIgnore;
import net.epsilony.mf.util.parm.MFParmOptional;
import net.epsilony.mf.util.parm.MFParmPackSetter;
import net.epsilony.mf.util.parm.MFParmNullPolicy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFHubInterceptor<T> implements MethodInterceptor {

    public static Logger                       logger                 = LoggerFactory.getLogger(MFHubInterceptor.class);

    private final Class<T>                     cls;
    private final T                            proxied;
    private Map<String, String>                methodNameToParameterName;
    private Map<String, Method>                parameterNameToWriteMethod;
    private Set<String>                        optionalParameters;
    private Map<String, WeakReference<Object>> parameterValueRecords  = new HashMap<>();
    private Map<String, WeakReference<Object>> parameterSourceRecords = new HashMap<>();
    private final Method                       hubPackSetterMethod;
    private final boolean                      defaultNullPolicy;
    private Object                             currentSource;

    public MFHubInterceptor(Class<T> cls) {
        this.cls = cls;
        MFHub mfHub = cls.getAnnotation(MFHub.class);
        if (null == mfHub) {
            throw new IllegalArgumentException("target object class is not annotated by " + MFHub.class);
        }

        defaultNullPolicy = targetDefaultNullPolicy();

        initByDescriptors();

        hubPackSetterMethod = getHubPackSetterMethod();

        proxied = generateProxied();

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

    public T generateProxied() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);
        @SuppressWarnings("unchecked")
        T _proxied = (T) enhancer.create();
        return _proxied;
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
        if (_hubSetterMethod == null) {
            throw new IllegalArgumentException("missing annotation: " + MFParmPackSetter.class);
        }
        return _hubSetterMethod;
    }

    private boolean targetDefaultNullPolicy() {
        MFParmNullPolicy nullPolicy = cls.getAnnotation(MFParmNullPolicy.class);
        return null != nullPolicy && nullPolicy.permit() == true;
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
        }

        if (Modifier.isAbstract(method.getModifiers())) {
            return null;
        }
        Object ret;
        if (isParameterSetter(method)) {
            checkNull(args[0], method);
            ret = proxy.invokeSuper(obj, args);
            recordParameterSetup(args[0], method);
        } else {
            ret = proxy.invokeSuper(obj, args);
        }

        return ret;
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
        String parameter = methodNameToparameterName(writeMethod.getName());
        recordParameterSetup(value, parameter);
    }

    private void recordParameterSetup(Object value, String parameter) {
        if (null != value) {
            parameterValueRecords.put(parameter, new WeakReference<Object>(value));
        } else {
            parameterValueRecords.put(parameter, null);
        }

        parameterSourceRecords.put(parameter, new WeakReference<Object>(currentSource));
        logger.info("{}: {}", parameter, value);
    }

    private void checkNull(Object value, Method writeMethod) {
        String parameter = methodNameToparameterName(writeMethod.getName());
        if (null == value) {
            boolean permitNull = isPermitNull(writeMethod);
            if (!permitNull) {
                throw new NullPointerException("null value is not permitted" + parameter);
            }
        }
    }

    private boolean isPermitNull(Method writeMethod) {
        MFParmNullPolicy nullPolicy = writeMethod.getAnnotation(MFParmNullPolicy.class);
        boolean permitNull = null != nullPolicy ? nullPolicy.permit() : defaultNullPolicy;
        return permitNull;
    }

    private boolean isHubSetter(Method method) {
        return hubPackSetterMethod.equals(method);
    }

    private String methodNameToparameterName(String methodName) {
        return methodNameToParameterName.get(methodName);
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
