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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.bus.WeakBus;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmIntrospector<T> {
    public static Logger                 logger                 = LoggerFactory.getLogger(MFParmIntrospector.class);

    private final Class<T>               cls;

    private Map<String, String>          methodNameToParameterName;
    private Map<String, Method>          parameterNameToWriteMethod;
    private Set<String>                  optionalParameters;

    private final Method                 packSetter;
    private final boolean                defaultNullPolicy;
    private Map<String, WeakBus<Object>> parameterNameToWeakBus = new HashMap<>();
    private final Method                 busPoolMethod;
    private final Method                 busPoolRegistryMethod;

    public MFParmIntrospector(Class<T> cls) {
        this.cls = cls;

        defaultNullPolicy = MFParmUtils.isNullPermitted(cls);

        initParameterMaps();

        packSetter = searchPackSetter();

        busPoolMethod = buildBusPool();

        busPoolRegistryMethod = MFParmUtils.searchBusPoolRegistry(cls);
    }

    private void initParameterMaps() {
        methodNameToParameterName = new HashMap<>();
        parameterNameToWriteMethod = new HashMap<>();
        optionalParameters = new HashSet<>();

        parameterNameToWriteMethod = MFParmUtils.searchParameterNameToSetter(cls);
        parameterNameToWriteMethod = Collections.unmodifiableMap(new LinkedHashMap<>(parameterNameToWriteMethod));

        for (Map.Entry<String, Method> entry : parameterNameToWriteMethod.entrySet()) {
            Method method = entry.getValue();
            String parameterName = entry.getKey();
            if (methodNameToParameterName.containsKey(method.getName())) {
                throw new IllegalStateException();
            }
            methodNameToParameterName.put(method.getName(), parameterName);
            if (method.isAnnotationPresent(MFParmOptional.class)) {
                optionalParameters.add(parameterName);
            }
        }

        optionalParameters = Collections.unmodifiableSet(new LinkedHashSet<>(optionalParameters));
        methodNameToParameterName = Collections.unmodifiableMap(new LinkedHashMap<>(methodNameToParameterName));

    }

    private Method searchPackSetter() {
        Method[] methods = cls.getMethods();
        Method _packSetter = null;
        for (Method method : methods) {
            if (!MFParmUtils.isPackSetter(method)) {
                continue;
            }

            if (_packSetter != null) {
                throw new IllegalArgumentException("MFHubSetter annotation is not unique");
            }
            if (methodNameToParameterName.containsKey(method.getName())) {
                throw new IllegalArgumentException("MFHubSetter cannot be annotated to parameter setter ("
                        + method.getName() + ")!");
            }
            _packSetter = method;
        }
        return _packSetter;
    }

    private Method buildBusPool() {
        buildLocalParameterNameToWeakBus();

        Method result = searchBusPoolMethod();
        if (result == null && !parameterNameToWeakBus.isEmpty()) {
            throw new IllegalArgumentException("missing @" + MFParmBusSource.class.getSimpleName());
        }

        if (result == null) {
            return null;
        }

        checkBusTriggers();

        MFParmBusPool busPool = result.getAnnotation(MFParmBusPool.class);
        String[] superBuses = busPool.superBuses();
        if (superBuses.length == 0) {
            return result;
        }

        for (String superBus : superBuses) {
            if (parameterNameToWeakBus.containsKey(superBus)) {
                throw new IllegalStateException("super bus and local bus parameter name conflicting : " + superBus);
            }
            parameterNameToWeakBus.put(superBus, new WeakBus<>(weakBusName(superBus)));
        }

        parameterNameToWeakBus = Collections.unmodifiableMap(new LinkedHashMap<>(parameterNameToWeakBus));
        return result;
    }

    private void buildLocalParameterNameToWeakBus() {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null || !readMethod.isAnnotationPresent(MFParmBusSource.class)) {
                continue;
            }

            String parameterName = descriptor.getName();
            WeakBus<Object> bus = new WeakBus<>(weakBusName(parameterName));
            parameterNameToWeakBus.put(parameterName, bus);
        }

    }

    private Method searchBusPoolMethod() {
        Method[] methods = cls.getMethods();
        Method result = null;
        for (Method method : methods) {
            if (!MFParmUtils.isBusPoolMethod(method)) {
                continue;
            }

            if (result != null) {
                throw new IllegalArgumentException("@" + MFParmBusPool.class.getSimpleName() + " is not unique");
            }

            result = method;
        }
        return result;
    }

    private String weakBusName(String parameterName) {
        return cls.getSimpleName() + ": " + parameterName;
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

    private boolean isBusTriggerMethod(Method method) {
        return method.isAnnotationPresent(MFParmBusTrigger.class);
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

    private String methodToParameterName(Method method) {
        return methodNameToParameterName.get(method.getName());
    }

    public Class<T> getTargetClass() {
        return cls;
    }

    public Map<String, String> getMethodNameToParameterName() {
        return methodNameToParameterName;
    }

    public Map<String, Method> getParameterNameToWriteMethod() {
        return parameterNameToWriteMethod;
    }

    public Set<String> getOptionalParameters() {
        return optionalParameters;
    }

    public Method getPackSetter() {
        return packSetter;
    }

    public boolean isDefaultNull() {
        return defaultNullPolicy;
    }

    public WeakBus<Object> getWeakBus(String parameterName) {
        return parameterNameToWeakBus.get(parameterName);
    }

    public Method getBusPoolMethod() {
        return busPoolMethod;
    }

    public Method getBusPoolRegistryMethod() {
        return busPoolRegistryMethod;
    }

    public boolean isParameterSetter(Method method) {
        return methodNameToParameterName.containsKey(method.getName());
    }

    public String getParameterName(Method method) {
        return methodNameToParameterName.get(method.getName());
    }

    public boolean isNullPermitted(Method method) {
        if (methodToParameterName(method) == null) {
            throw new IllegalArgumentException(method + "is not a parameter setter");
        }
        return MFParmUtils.isNullPermitted(defaultNullPolicy, method);
    }

}
