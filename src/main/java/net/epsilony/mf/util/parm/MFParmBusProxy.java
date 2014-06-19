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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.epsilony.mf.util.MFUtils;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.parm.ann.MFParmAsSubBus;
import net.epsilony.mf.util.parm.ann.MFParmBusTrigger;
import net.epsilony.mf.util.parm.ann.MFParmWithBusProxy;
import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface MFParmBusProxy {

    Map<String, BusEntry> getParmNameToBusEntry();

    default WeakBus<Object> getWeakBus(String key) {
        BusEntry busEntry = getBusEntry(key);
        if (null == busEntry) {
            return null;
        }
        return busEntry.getWeakBus();
    }

    BusEntry getBusEntry(String parmName);

    public interface BusEntry {
        WeakBus<Object> getWeakBus();

        Method getGetter();

        Set<Method> getTriggers();

        Set<Method> getUnInvokedTriggers();

        default boolean isGlobal() {
            return MFParmUtils.isGlobal(getGetter());
        }
    }

    void registerToWeakBus(String parmName, Method writeMethod, Object bean);

    Set<String> registerToWeakBuses(Object registerObj, boolean OnlyGlobal);

    default Set<String> registerToWeakBuses(Object registerObj) {
        return registerToWeakBuses(registerObj, true);
    }

    static class SimpBusEntry implements BusEntry {

        private WeakBus<Object> weakBus;
        private Method          getter;
        private Set<Method>     triggers = new HashSet<>();
        private Set<Method>     unInvokedTriggers;

        public void finishPreparing() {
            triggers = Collections.unmodifiableSet(triggers);
            unInvokedTriggers = new HashSet<>(triggers);
        }

        private void createWeakBus(Object bean) {
            String name = String.format("%s@%x", bean.getClass().getSimpleName(), bean.hashCode());
            weakBus = new WeakBus<>(name);
        }

        @Override
        public WeakBus<Object> getWeakBus() {
            return weakBus;
        }

        @Override
        public Method getGetter() {
            return getter;
        }

        public void setGetter(Method getter) {
            this.getter = getter;
        }

        @Override
        public Set<Method> getTriggers() {
            return triggers;
        }

        @Override
        public Set<Method> getUnInvokedTriggers() {
            return unInvokedTriggers;
        }

        @Override
        public boolean isGlobal() {
            return MFParmUtils.isGlobal(getter);
        }

    }

    public static class MFParmBusProxyImp implements MFParmBusProxy {

        public static final Logger    logger = LoggerFactory.getLogger(MFParmBusProxyImp.class);

        private Object                target;
        private Map<String, BusEntry> parmNameToBusEntry;

        public MFParmBusProxyImp(Object target) {

            this.target = target;

            MFParmWithBusProxy withBusProxy = targetClass().getAnnotation(MFParmWithBusProxy.class);
            if (null == withBusProxy) {
                throw new IllegalArgumentException(targetClass().getSimpleName() + "is not annotated by @"
                        + MFParmWithBusProxy.class.getSimpleName());
            }
            fillMapByTriggers();

            PropertyUtils.getPropertyDescriptors(getClass());
        }

        private Class<? extends Object> targetClass() {
            return target.getClass();
        }

        private void fillMapByTriggers() {
            parmNameToBusEntry = new LinkedHashMap<>();

            Map<String, Method> parameterNameToGetter = MFParmUtils.searchParameterNameToGetter(targetClass());

            for (Method method : targetClass().getMethods()) {
                if (MFParmUtils.isSetter(method) == false) {
                    continue;
                }
                Method setter = method;

                if (!method.isAnnotationPresent(MFParmBusTrigger.class)) {
                    continue;
                }

                String[] triggerAims = MFParmUtils.getTriggerAims(setter);
                for (String aim : triggerAims) {
                    SimpBusEntry busEntry = (SimpBusEntry) parmNameToBusEntry.get(aim);
                    if (null == busEntry) {
                        busEntry = new SimpBusEntry();

                        Method getter = parameterNameToGetter.get(aim);
                        if (getter == null) {
                            throw new IllegalStateException("cannot find getter of " + aim + " in " + target.getClass());
                        }
                        busEntry.setGetter(getter);
                        busEntry.createWeakBus(target);
                        parmNameToBusEntry.put(aim, busEntry);

                    }
                    busEntry.getTriggers().add(setter);
                }
            }

            for (BusEntry busEntry : new HashSet<>(parmNameToBusEntry.values())) {
                SimpBusEntry simpBusEntry = (SimpBusEntry) busEntry;
                simpBusEntry.finishPreparing();
            }

            for (Method method : target.getClass().getMethods()) {
                MFParmUtils.checkMethod(method);
            }
        }

        public void triggerMethodInvoked(Method method) {
            MFParmBusTrigger busTrigger = method.getAnnotation(MFParmBusTrigger.class);
            if (null == busTrigger) {
                throw new IllegalArgumentException(method + " should be annotated by @"
                        + MFParmBusTrigger.class.getSimpleName());
            }

            String[] triggerAims = MFParmUtils.getTriggerAims(method);

            for (String parmName : triggerAims) {
                BusEntry entry = parmNameToBusEntry.get(parmName);
                WeakBus<Object> weakBus = entry.getWeakBus();
                Set<Method> unInvokedTriggers = entry.getUnInvokedTriggers();
                unInvokedTriggers.remove(method);
                if (!unInvokedTriggers.isEmpty()) {
                    continue;
                }

                weakBus.postToEach(() -> {
                    try {
                        return entry.getGetter().invoke(target);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });

            }
        }

        @Override
        public BusEntry getBusEntry(String parmName) {
            return parmNameToBusEntry.get(parmName);
        }

        @Override
        public Set<String> registerToWeakBuses(Object bean, boolean onlyGlobal) {

            Class<?> originClass = bean.getClass();
            while (Enhancer.isEnhanced(originClass)) {
                originClass = originClass.getSuperclass();
            }
            Set<String> registered = new LinkedHashSet<>();
            Map<String, Method> beanParmNameToSetter = MFParmUtils.searchParameterNameToSetter(bean.getClass());
            for (Map.Entry<String, Method> entry : beanParmNameToSetter.entrySet()) {

                Method writeMethod = entry.getValue();
                if (onlyGlobal && MFParmUtils.isLocal(writeMethod)) {
                    continue;
                }
                String parameterName = entry.getKey();
                BusEntry busEntry = getBusEntry(parameterName);
                if (null == busEntry) {
                    continue;
                }
                if (onlyGlobal && !busEntry.isGlobal()) {
                    continue;
                }
                registerToWeakBus(parameterName, writeMethod, bean);
                registered.add(parameterName);
            }
            return registered;
        }

        @Override
        public void registerToWeakBus(String parmName, Method writeMethod, Object bean) {
            WeakBus<Object> weakBus = getWeakBus(parmName);
            if (null == weakBus) {
                throw new IllegalStateException(parmName);
            }

            boolean isAsSubBus = writeMethod.isAnnotationPresent(MFParmAsSubBus.class);
            if (isAsSubBus) {
                if (!writeMethod.getParameterTypes()[0].equals(Supplier.class)) {
                    throw new IllegalStateException("setter parameter is not consumer, method: " + writeMethod);
                }
                final Method beanMethod;
                try {
                    beanMethod = bean.getClass().getMethod(writeMethod.getName(), writeMethod.getParameterTypes());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new IllegalStateException(e);
                }
                weakBus.registerSubBus((obj, supplier) -> {
                    try {
                        beanMethod.invoke(obj, supplier);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }, bean);

            } else {
                weakBus.register((consumer, value) -> {
                    try {
                        writeMethod.invoke(consumer, value);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }, bean);
            }

            String beanString = MFUtils.simpToString(bean);
            String targetString = MFUtils.simpToString(target);
            logger.info("register : {}<-{}.{}(as sub bus: {})", targetString, beanString, parmName, isAsSubBus);
        }

        @Override
        public Map<String, BusEntry> getParmNameToBusEntry() {
            return parmNameToBusEntry;
        }

    }

}
