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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmIntrospector<T> {
    public static Logger                 logger = LoggerFactory.getLogger(MFParmIntrospector.class);

    private final Class<T>               cls;

    private Map<String, String>          methodNameToParameterName;
    private Map<String, Method>          parameterNameToWriteMethod;
    private Map<String, Method>          parameterNameToReadMethod;
    private Set<String>                  optionalParameters;

    private final Method                 packSetter;
    private final boolean                defaultNullPolicy;
    private Map<String, WeakBus<Object>> parameterNameToWeakBus;
    private Map<String, String>          busAliasToParameterName;
    private final Method                 busPoolMethod;
    private final Method                 busPoolRegistryMethod;

    public MFParmIntrospector(Class<T> cls) {
        this.cls = cls;

        defaultNullPolicy = MFParmUtils.isNullPermitted(cls);

        initParameterMaps();

        packSetter = searchPackSetter();

        busPoolMethod = MFParmUtils.searchBusPool(cls);

        buildBusPool();

        busPoolRegistryMethod = MFParmUtils.searchBusPoolRegistry(cls);
    }

    private void initParameterMaps() {

        parameterNameToWriteMethod = MFParmUtils.searchParameterNameToSetter(cls);
        parameterNameToWriteMethod = Collections.unmodifiableMap(new LinkedHashMap<>(parameterNameToWriteMethod));

        parameterNameToReadMethod = MFParmUtils.searchParameterNameToGetter(cls);
        parameterNameToReadMethod = Collections.unmodifiableMap(new LinkedHashMap<>(parameterNameToReadMethod));

        methodNameToParameterName = new HashMap<>();
        List<Map.Entry<String, Method>> entries = new ArrayList<>(parameterNameToWriteMethod.entrySet());
        entries.addAll(parameterNameToReadMethod.entrySet());
        for (Map.Entry<String, Method> entry : entries) {
            Method method = entry.getValue();
            String parameterName = entry.getKey();
            if (methodNameToParameterName.containsKey(method.getName())) {
                throw new IllegalStateException();
            }
            methodNameToParameterName.put(method.getName(), parameterName);
        }

        optionalParameters = new HashSet<>();
        for (Method method : cls.getMethods()) {
            if (!method.isAnnotationPresent(MFParmOptional.class)) {
                continue;
            }
            String parameterName = getParameterName(method);
            if (!parameterNameToWriteMethod.containsKey(parameterName)) {
                throw new IllegalArgumentException("@" + MFParmOptional.class.getSimpleName()
                        + " should only be on a property setter setter, not " + method);
            }
            optionalParameters.add(parameterName);
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

    private void buildBusPool() {

        buildParameterNameToWeakBusFromTriggerDemand();

        searchBusAlias();

        if (busPoolMethod == null && !parameterNameToWeakBus.isEmpty()) {
            throw new IllegalArgumentException(cls.getSimpleName() + " is lack of @"
                    + MFParmBusPool.class.getSimpleName());
        }

        if (busPoolMethod == null) {
            return;
        }

        MFParmBusPool busPool = busPoolMethod.getAnnotation(MFParmBusPool.class);
        String[] superBuses = busPool.superBuses();
        if (superBuses.length == 0) {
            return;
        }

        for (String superBus : superBuses) {
            if (parameterNameToWeakBus.containsKey(superBus)) {
                throw new IllegalStateException("super bus and local bus parameter name conflicting : " + superBus);
            }
            parameterNameToWeakBus.put(superBus, new WeakBus<>(weakBusName(superBus)));
        }

        parameterNameToWeakBus = Collections.unmodifiableMap(new LinkedHashMap<>(parameterNameToWeakBus));
        return;
    }

    private void buildParameterNameToWeakBusFromTriggerDemand() {
        parameterNameToWeakBus = new HashMap<>();
        Map<Method, List<String>> missings = new HashMap<>();
        for (Method method : cls.getMethods()) {
            if (!isBusTriggerMethod(method)) {
                continue;
            }
            String[] triggerMethodAims = getBusTriggerMethodAims(method);
            List<String> missing = new ArrayList<>();
            for (String busParameter : triggerMethodAims) {
                Method busValueGetter = parameterNameToReadMethod.get(busParameter);
                if (null == busValueGetter) {
                    missing.add(busParameter);
                    continue;
                }
                if (parameterNameToWeakBus.containsKey(busParameter)) {
                    continue;
                }
                parameterNameToWeakBus.put(busParameter, new WeakBus<>(weakBusName(busParameter)));
            }
            if (!missing.isEmpty()) {
                missings.put(method, missing);
            }
        }

        if (missings.isEmpty()) {
            return;
        }

        StringBuilder exceptionMessge = new StringBuilder();
        exceptionMessge.append("missing getters as bus source: ");

        for (Map.Entry<Method, List<String>> entry : missings.entrySet()) {
            Method method = entry.getKey();
            List<String> miss = entry.getValue();
            exceptionMessge.append("[trigger " + method + " missing: " + miss + "];");
        }

        throw new IllegalStateException(exceptionMessge.toString());
    }

    private void searchBusAlias() {
        busAliasToParameterName = new HashMap<>();
        for (Method method : cls.getMethods()) {
            MFParmBusAlias busAlias = method.getAnnotation(MFParmBusAlias.class);
            if (null == busAlias) {
                continue;
            }
            if (!isParameterGetter(method)) {
                if (!isParameterSetter(method)) {
                    throw new IllegalArgumentException("@" + MFParmBusAlias.class.getSimpleName()
                            + " should be only on setter of getter, not" + method);
                }
                continue;
            }
            if (getWeakBus(getParameterName(method)) == null) {
                throw new IllegalArgumentException("@" + MFParmBusAlias.class.getSimpleName()
                        + " is on a getter which is lack of trigger (method : " + method + ")");
            }

            String alias = busAlias.value();
            busAliasToParameterName.put(alias, getParameterName(method));
        }
        busAliasToParameterName = Collections.unmodifiableMap(new LinkedHashMap<>(busAliasToParameterName));
    }

    public String[] getBusTriggerMethodAims(Method method) {
        MFParmBusTrigger busTrigger = method.getAnnotation(MFParmBusTrigger.class);
        if (null == busTrigger) {
            throw new IllegalArgumentException("not @" + MFParmBusTrigger.class.getSimpleName() + " annotated");
        }
        String[] value = busTrigger.value();
        if (value.length == 0) {
            String parameterName = getParameterName(method);
            if (null == parameterName) {
                throw new IllegalArgumentException("@" + MFParmBusTrigger.class.getSimpleName()
                        + ".value must be set when the annotation aim is not a bean setter (" + method + ")");
            }
            value = new String[] { parameterName };
        }
        return value;
    }

    private String weakBusName(String parameterName) {
        return cls.getSimpleName() + ": " + parameterName;
    }

    private boolean isBusTriggerMethod(Method method) {
        return method.isAnnotationPresent(MFParmBusTrigger.class);
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

    public WeakBus<Object> getWeakBus(String name) {
        String aliased = busAliasToParameterName.get(name);
        if (null != aliased) {
            return parameterNameToWeakBus.get(aliased);
        }
        return parameterNameToWeakBus.get(name);
    }

    public Method getBusPoolMethod() {
        return busPoolMethod;
    }

    public Method getBusPoolRegistryMethod() {
        return busPoolRegistryMethod;
    }

    public boolean isParameterSetter(Method method) {
        String parameterName = methodNameToParameterName.get(method.getName());
        if (null == parameterName) {
            return false;
        }
        return method.equals(parameterNameToWriteMethod.get(parameterName));
    }

    public boolean isParameterGetter(Method method) {
        String parameterName = methodNameToParameterName.get(method.getName());
        if (null == parameterName) {
            return false;
        }
        return method.equals(parameterNameToReadMethod.get(parameterName));
    }

    public String getParameterName(Method method) {
        return methodNameToParameterName.get(method.getName());
    }

    public boolean isNullPermitted(Method method) {
        if (getParameterName(method) == null) {
            throw new IllegalArgumentException(method + "is not a parameter setter");
        }
        return MFParmUtils.isNullPermitted(defaultNullPolicy, method);
    }

}
