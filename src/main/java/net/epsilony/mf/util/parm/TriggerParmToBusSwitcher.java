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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.epsilony.mf.util.bus.MethodBiConsumerInvoker;
import net.epsilony.mf.util.bus.MethodSubBusBiConsumerInvoker;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.parm.ann.AsSubBus;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriggerParmToBusSwitcher {
    private Map<String, MFParmEntry>  parmEntries     = new LinkedHashMap<>();
    private String                    _lastParmName   = null;
    private MFParmEntry               _lastParmEntry  = null;

    private Map<String, WeakBusEntry> busEntries      = new HashMap<>();
    private String                    _lastBusName    = null;
    private WeakBusEntry              _lastBusEntry   = null;

    private Set<String>               untriggeredParm = new LinkedHashSet<>();

    public void addTriggerParm(String parm, String[] aims) {
        MFParmEntry parmEntry = new MFParmEntry(aims);
        parmEntries.put(parm, parmEntry);
        _lastParmName = parm;
        _lastParmEntry = parmEntry;
        updateBusesByAims(parm, aims);
        untriggeredParm.add(parm);
    }

    public void triggerParmAims(String parm) {
        MFParmEntry parmEntry = getParmEntry(parm);
        List<String> triggerAims = parmEntry.getTriggerAims();
        for (String aim : triggerAims) {
            getBusEntry(aim).trigger(parm);
        }
        untriggeredParm.remove(parm);
    }

    private MFParmEntry getParmEntry(String parm) {
        if (_lastParmName == parm) {
            return _lastParmEntry;
        }
        MFParmEntry result = parmEntries.get(parm);
        _lastParmName = parm;
        _lastParmEntry = result;
        return result;
    }

    private void updateBusesByAims(String parm, String[] aims) {
        for (String aim : aims) {
            WeakBusEntry busEntry = getOrInitBusEntry(aim);
            busEntry.addTrigger(parm);
        }
    }

    private WeakBusEntry getOrInitBusEntry(String busName) {
        if (null != _lastBusName && busName == _lastBusName) {
            return _lastBusEntry;
        }
        WeakBusEntry result = busEntries.get(busName);
        if (null == result) {
            result = new WeakBusEntry(busName);
            busEntries.put(busName, result);
        }
        _lastBusName = busName;
        _lastBusEntry = result;
        return result;
    }

    private WeakBusEntry getBusEntry(String busName) {
        if (busName == _lastBusName) {
            return _lastBusEntry;
        }
        WeakBusEntry result = busEntries.get(busName);
        _lastBusName = busName;
        _lastBusEntry = result;
        return result;
    }

    public Set<String> getTriggerParms() {
        return parmEntries.keySet();
    }

    public Set<String> getUntriggeredParms() {
        return untriggeredParm;
    }

    public Set<String> getBusNames() {
        return busEntries.keySet();
    }

    public WeakBus<Object> getBus(String busName) {
        return getBusEntry(busName).getWeakBus();
    }

    public boolean isBusGlobal(String busName) {
        return getBusEntry(busName).isGlobal();
    }

    public void setBusGlobal(String busName, boolean local) {
        getBusEntry(busName).setGlobal(local);
    }

    public Supplier<? extends Object> getBusValueSource(String busName) {
        return getBusEntry(busName).getValueSource();
    }

    public void setBusValueSource(String busName, Supplier<? extends Object> valueSource) {
        getBusEntry(busName).setValueSource(valueSource);
    }

    public Set<String> getBusTriggers(String busName) {
        return getBusEntry(busName).getTriggers();
    }

    public Set<String> getBusUninvokedTriggers(String busName) {
        return getBusEntry(busName).getUninvokedTriggers();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, V> boolean register(String parm, BiConsumer<? super T, V> biConsumer, T bean) {
        WeakBus<Object> weakBus = getBus(parm);
        if (weakBus == null) {
            return false;
        }
        weakBus.register((BiConsumer) biConsumer, bean);
        return true;
    }

    public boolean register(String parm, Method method, Object bean) {
        return register(parm, getMethodInvoker(method), bean);
    }

    public boolean register(String parm, Object bean) {
        Method method = getParmSetter(parm, bean.getClass());
        if (null == method) {
            return false;
        }
        return register(parm, method, bean);
    }

    public void autoRegister(Object bean, boolean globalOnly) {
        Map<String, Method> parmToSetter = getParmToSetter(bean.getClass());
        for (Map.Entry<String, Method> mapEntry : parmToSetter.entrySet()) {
            Method method = mapEntry.getValue();
            String parmName = mapEntry.getKey();
            if (globalOnly && !isBusGlobal(parmName)) {
                continue;
            }
            if (method.isAnnotationPresent(AsSubBus.class)) {
                registerAsSubBus(parmName, method, bean);
            } else {
                register(parmName, method, bean);
            }
        }
    }

    public void autoRegister(Object bean) {
        autoRegister(bean, false);
    }

    private Method getParmSetter(String parm, Class<? extends Object> beanClass) {
        return MFParmUtils.searchParameterNameToSetter(beanClass).get(parm);
    }

    private Map<String, Method> getParmToSetter(Class<? extends Object> beanClass) {
        return MFParmUtils.searchParameterNameToSetter(beanClass);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, V> boolean registerAsSubBus(String parm, BiConsumer<? super T, Supplier<? extends V>> subBusBiConsumer,
            T subBusBean) {
        WeakBus<Object> weakBus = getBus(parm);
        if (weakBus == null) {
            return false;
        }
        weakBus.registerSubBus((BiConsumer) subBusBiConsumer, subBusBean);
        return true;
    }

    public boolean registerAsSubBus(String parm, Method method, Object subBusBean) {
        return registerAsSubBus(parm, getSubBusMethodInvoker(method), subBusBean);
    }

    public boolean registerAsSubBus(String parm, Object subBusBean) {
        Method method = getParmSetter(parm, subBusBean.getClass());
        return registerAsSubBus(parm, method, subBusBean);
    }

    private BiConsumer<Object, Object> getMethodInvoker(Method method) {
        return new MethodBiConsumerInvoker(method);
    }

    private BiConsumer<Object, Supplier<? extends Object>> getSubBusMethodInvoker(Method method) {
        return new MethodSubBusBiConsumerInvoker(method);
    }

    private static class MFParmEntry {
        private List<String> triggerAims = Collections.emptyList();

        public MFParmEntry(String[] aims) {
            if (aims.length == 0) {
                return;
            }
            triggerAims = Collections.unmodifiableList(Arrays.asList(aims));
        }

        public List<String> getTriggerAims() {
            return triggerAims;
        }

    }

    private static class WeakBusEntry {
        private String                     name;
        private Supplier<? extends Object> valueSource;
        private boolean                    global;

        private Set<String>                triggers          = new HashSet<>();
        private Set<String>                uninvokedTriggers = new HashSet<>();
        private WeakBus<Object>            weakBus;

        private WeakBusEntry(String name) {
            this.name = name;
        }

        public void trigger(String trigger) {
            if (!triggers.contains(trigger)) {
                throw new IllegalArgumentException();
            }
            uninvokedTriggers.remove(trigger);
            if (!uninvokedTriggers.isEmpty()) {
                return;
            }
            getWeakBus().postToEach(valueSource);
        }

        // public String getName() {
        // return name;
        // }
        //
        public Supplier<? extends Object> getValueSource() {
            return valueSource;
        }

        public void setValueSource(Supplier<? extends Object> valueSource) {
            this.valueSource = valueSource;
        }

        @Override
        public String toString() {
            return "WeakBusEntry [name=" + name + ", local=" + global + ", triggers=" + triggers
                    + ", uninvokedTriggers=" + uninvokedTriggers + "]";
        }

        public Set<String> getTriggers() {
            return triggers;
        }

        public void addTrigger(String trigger) {
            triggers.add(trigger);
            uninvokedTriggers.add(trigger);
        }

        public Set<String> getUninvokedTriggers() {
            return uninvokedTriggers;
        }

        private WeakBus<Object> getOrInitWeakBus() {
            if (null == weakBus) {
                weakBus = new WeakBus<>(String.format("%s local:[%b] triggers:%s", name, global, triggers));
            }
            return weakBus;
        }

        public WeakBus<Object> getWeakBus() {
            return getOrInitWeakBus();
        }

        public boolean isGlobal() {
            return global;
        }

        public void setGlobal(boolean local) {
            this.global = local;
        }
    }

}
