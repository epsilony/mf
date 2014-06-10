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
package net.epsilony.mf.util.parm;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.parm.ann.MFParmBusAlias;
import net.epsilony.mf.util.parm.ann.MFParmBusTrigger;
import net.epsilony.mf.util.parm.ann.MFParmWithBusPool;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface MFParmBusPool {

    Map<String, WeakBus<Object>> parmNameToWeakBus();

    Map<String, String> aliasToParmName();

    String[] getBusTriggerMethodAims(Method method);

    WeakBus<Object> weakBus(String name);

    void registerToWeakBus(Object registerObj);

    void registerToWeakBus(String key, Object registerObj);

    public static class MFParmBusPoolImp implements MFParmBusPool {

        public static final Logger           logger = LoggerFactory.getLogger(MFParmBusPoolImp.class);

        private MFParmIndexer                parmIndexer;
        private Object                       target;
        private Map<String, WeakBus<Object>> parmNameToWeakBus;
        private Map<String, String>          aliasToParmName;
        private Map<String, Set<Method>>     triggerGroupsSetting;
        private Map<String, Set<Method>>     triggerGroups;

        public MFParmBusPoolImp(MFParmIndexer parmIndexer, Object target) {
            this.parmIndexer = parmIndexer;
            this.target = target;
            if (!(parmIndexer.getTargetClass().isInstance(target))) {
                throw new IllegalArgumentException("target [" + target + "] is not instanceof "
                        + parmIndexer.getTargetClass());
            }

            parmNameToWeakBus = new HashMap<>();

            MFParmWithBusPool withBusPool = parmIndexer.getTargetClass().getAnnotation(MFParmWithBusPool.class);
            if (null == withBusPool) {
                throw new IllegalArgumentException(parmIndexer.getClass().getSimpleName() + "is not annotated by @"
                        + MFParmWithBusPool.class.getSimpleName());
            }
            fillMapByTriggers();

            buildAlias();

            String[] superBuses = withBusPool.superBuses();
            if (superBuses.length == 0) {
                return;
            }

            for (String superBus : superBuses) {
                if (parmNameToWeakBus.containsKey(superBus)) {
                    throw new IllegalStateException("super bus and local bus parameter name conflicting : " + superBus);
                }
                parmNameToWeakBus.put(superBus, new WeakBus<>(weakBusName(superBus)));
            }

            parmNameToWeakBus = Collections.unmodifiableMap(new LinkedHashMap<>(parmNameToWeakBus));
        }

        private void fillMapByTriggers() {
            triggerGroupsSetting = new LinkedHashMap<>();
            Map<Method, List<String>> missings = new HashMap<>();
            for (Method method : parmIndexer.getTargetClass().getMethods()) {
                if (!isBusTriggerMethod(method)) {
                    continue;
                }

                tryPutToTriggerGroups(method);

                String[] triggerMethodAims = getBusTriggerMethodAims(method);
                List<String> missing = new ArrayList<>();
                for (String busParameter : triggerMethodAims) {
                    Method busValueGetter = parmIndexer.getGetter(busParameter);
                    if (null == busValueGetter) {
                        missing.add(busParameter);
                        continue;
                    }
                    if (parmNameToWeakBus.containsKey(busParameter)) {
                        continue;
                    }
                    parmNameToWeakBus.put(busParameter, new WeakBus<>(weakBusName(busParameter)));
                }
                if (!missing.isEmpty()) {
                    missings.put(method, missing);
                }
            }

            Set<String> groups = new LinkedHashSet<>(triggerGroupsSetting.keySet());
            for (String group : groups) {
                Set<Method> groupMethods = triggerGroupsSetting.get(group);
                groupMethods = Collections.unmodifiableSet(groupMethods);
                triggerGroupsSetting.put(group, groupMethods);
            }
            triggerGroupsSetting = Collections.unmodifiableMap(triggerGroupsSetting);

            triggerGroups = cloneTriggerGroupsSetting();

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

        private void tryPutToTriggerGroups(Method method) {
            MFParmBusTrigger busTrigger = method.getAnnotation(MFParmBusTrigger.class);
            String group = busTrigger.group();
            if (group.isEmpty()) {
                return;
            }
            Set<Method> groupMethods = triggerGroupsSetting.get(group);
            if (groupMethods == null) {
                groupMethods = new LinkedHashSet<>();
                groupMethods.add(method);
                triggerGroupsSetting.put(group, groupMethods);
            } else {
                groupMethods.add(method);
            }
        }

        public void triggerMethodInvoked(Method method) {
            MFParmBusTrigger busTrigger = method.getAnnotation(MFParmBusTrigger.class);
            if (null == busTrigger) {
                throw new IllegalArgumentException(method + " should be annotated by @"
                        + MFParmBusTrigger.class.getSimpleName());
            }
            String group = busTrigger.group();
            Set<Method> triggerGroup = null;
            if (!group.isEmpty()) {
                triggerGroup = triggerGroups.get(group);
            }

            if (null != triggerGroup) {
                triggerGroup.remove(method);
                if (!triggerGroup.isEmpty()) {
                    return;
                } else {
                    triggerGroup.remove(group);
                }
            }

            List<String> triggerAims = null;
            if (group.isEmpty()) {
                triggerAims = Arrays.asList(getBusTriggerMethodAims(method));
            } else {
                Set<Method> methodGroup = triggerGroupsSetting.get(group);
                triggerAims = new ArrayList<>();
                for (Method trigger : methodGroup) {
                    triggerAims.addAll(Arrays.asList(getBusTriggerMethodAims(trigger)));
                }
            }

            BeanMap beanMap = new BeanMap(target);
            for (String parmName : triggerAims) {
                WeakBus<Object> weakBus = weakBus(parmName);
                weakBus.postToEach(() -> beanMap.get(parmName));
            }
        }

        public Map<String, Set<Method>> cloneTriggerGroupsSetting() {
            Map<String, Set<Method>> result = new HashMap<>();
            for (Map.Entry<String, Set<Method>> entry : triggerGroupsSetting.entrySet()) {
                result.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            return result;
        }

        public boolean isBusTriggerMethod(Method method) {
            return method.isAnnotationPresent(MFParmBusTrigger.class);
        }

        @Override
        public String[] getBusTriggerMethodAims(Method method) {
            MFParmBusTrigger busTrigger = method.getAnnotation(MFParmBusTrigger.class);
            if (null == busTrigger) {
                throw new IllegalArgumentException("not @" + MFParmBusTrigger.class.getSimpleName() + " annotated");
            }
            String[] value = busTrigger.value();

            if (value.length == 0) {
                String parameterName = parmIndexer.getParmName(method);
                if (null == parameterName) {
                    throw new IllegalArgumentException("@" + MFParmBusTrigger.class.getSimpleName()
                            + ".value must be set when the annotation aim is not a bean setter (" + method + ")");
                }
                value = new String[] { parameterName };
            }
            return value;
        }

        private void buildAlias() {
            aliasToParmName = new HashMap<>();
            for (Method method : parmIndexer.getTargetClass().getMethods()) {
                MFParmBusAlias busAlias = method.getAnnotation(MFParmBusAlias.class);
                if (null == busAlias) {
                    continue;
                }
                if (!parmIndexer.isGetter(method)) {
                    if (!parmIndexer.isSetter(method)) {
                        throw new IllegalArgumentException("@" + MFParmBusAlias.class.getSimpleName()
                                + " should be only on setter or getter, not " + method);
                    }
                    continue;
                }
                if (weakBus(parmIndexer.getParmName(method)) == null) {
                    throw new IllegalArgumentException("@" + MFParmBusAlias.class.getSimpleName()
                            + " is on a getter which is lack of trigger (method : " + method + ")");
                }

                String alias = busAlias.value();
                aliasToParmName.put(alias, parmIndexer.getParmName(method));
            }
            aliasToParmName = Collections.unmodifiableMap(new LinkedHashMap<>(aliasToParmName));
        }

        private String weakBusName(String parameterName) {
            return parmIndexer.getTargetClass().getSimpleName() + ": " + parameterName;
        }

        @Override
        public Map<String, WeakBus<Object>> parmNameToWeakBus() {
            return parmNameToWeakBus;
        }

        @Override
        public Map<String, String> aliasToParmName() {
            return aliasToParmName;
        }

        @Override
        public WeakBus<Object> weakBus(String key) {
            String parmName = aliasToParmName.get(key);
            if (null == parmName) {
                parmName = key;
            }
            return parmNameToWeakBus.get(parmName);
        }

        @Override
        public void registerToWeakBus(Object bean) {

            Map<String, Method> beanParmNameToSetter = MFParmUtils.searchParameterNameToSetter(bean.getClass());
            for (Map.Entry<String, Method> entry : beanParmNameToSetter.entrySet()) {
                String parameterName = entry.getKey();
                Method writeMethod = entry.getValue();
                registerToWeakBus(parameterName, writeMethod, bean);
            }
        }

        private void registerToWeakBus(String parmName, Method writeMethod, Object bean) {
            MFParmBusAlias busAlias = writeMethod.getAnnotation(MFParmBusAlias.class);
            String busKey = busAlias == null ? parmName : busAlias.value();

            WeakBus<Object> weakBus = weakBus(busKey);
            if (null == weakBus) {
                return;
            }

            weakBus.register((consumer, value) -> {
                try {
                    writeMethod.invoke(consumer, value);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }, bean);

            Class<?> cls = parmIndexer.getTargetClass();
            String beanString = String.format("%s@%x", bean.getClass().getSimpleName(), bean.hashCode());
            if (null == busAlias) {
                logger.info("register : {}->{}.{}", cls.getSimpleName(), beanString, parmName);
            } else {
                logger.info("register : {}->{}.{}(as alias: {})", cls.getSimpleName(), beanString, parmName,
                        busAlias.value());
            }
        }

        @Override
        public void registerToWeakBus(String parmName, Object bean) {
            Method writeMethod = null;
            try {
                PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, parmName);
                writeMethod = propertyDescriptor.getWriteMethod();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
            if (null == writeMethod) {
                return;
            }
            registerToWeakBus(parmName, writeMethod, bean);
        }

    }

}
