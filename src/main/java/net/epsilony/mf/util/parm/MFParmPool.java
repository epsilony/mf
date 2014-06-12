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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.parm.MFParmBusProxy.BusEntry;
import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmPool {
    public static Logger logger = LoggerFactory.getLogger(MFParmPool.class);

    public interface BeanSetterEntry {
        Object getBean();

        Method getSetter();
    }

    static class SimpBeanSetterEntry implements BeanSetterEntry {
        Object bean;
        Method setter;

        @Override
        public Object getBean() {
            return bean;
        }

        public void setBean(Object bean) {
            this.bean = bean;
        }

        @Override
        public Method getSetter() {
            return setter;
        }

        public void setSetter(Method setter) {
            this.setter = setter;
        }
    }

    private Map<String, MFParmBusProxy>  parmNameToGlobalBusProxy;
    private Map<String, BeanSetterEntry> parmNameToOpenBeanSetterEntry;
    private List<Object>                 beans;

    public List<Object> getBeans() {
        return beans;
    }

    public void setBeans(List<? extends Object> beans) {
        this.beans = Collections.unmodifiableList(new ArrayList<>(beans));
        init();
    }

    public BeanSetterEntry getOpenBeanSetterEntry(String parmName) {
        return parmNameToOpenBeanSetterEntry.get(parmName);
    }

    public Object getOpenParmBean(String parmName) {
        BeanSetterEntry openBeanSetterEntry = getOpenBeanSetterEntry(parmName);
        if (null == openBeanSetterEntry) {
            return null;
        }
        return openBeanSetterEntry.getBean();
    }

    public Method getOpenParmGetter(String parmName) {
        BeanSetterEntry openBeanSetterEntry = getOpenBeanSetterEntry(parmName);
        if (null == openBeanSetterEntry) {
            return null;
        }
        return openBeanSetterEntry.getSetter();
    }

    public Map<String, MFParmBusProxy> getParmNameToGlobalBusProxy() {
        return parmNameToGlobalBusProxy;
    }

    public Map<String, BeanSetterEntry> getParmNameToOpenBeanSetterEntry() {
        return parmNameToOpenBeanSetterEntry;
    }

    public void setupByMap(Map<String, Object> beanMap) {
        Set<String> keySet = beanMap.keySet();
        for (Object key : keySet) {
            setup((String) key, beanMap.get(key));
        }
    }

    public void setup(Object bean) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, Object> beanMap = (Map) new BeanMap(bean);
        setupByMap(beanMap);
    }

    public void setup(String name, Object value) {
        BeanSetterEntry beanSetterEntry = parmNameToOpenBeanSetterEntry.get(name);
        if (beanSetterEntry == null) {
            return;
        }
        Object bean = beanSetterEntry.getBean();
        Method setter = beanSetterEntry.getSetter();
        try {
            setter.invoke(bean, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        logger.info("recieved {}", name);
    }

    private void init() {
        parmNameToGlobalBusProxy = new LinkedHashMap<>();
        parmNameToOpenBeanSetterEntry = new LinkedHashMap<>();
        for (Object bean : beans) {
            if (!(bean instanceof MFParmBusProxy)) {
                continue;
            }
            recordGlobalBuses((MFParmBusProxy) bean);
        }

        for (Object bean : beans) {
            tryConnectToBusesAndRecordOpenSetters(bean);
        }
        parmNameToGlobalBusProxy = Collections.unmodifiableMap(parmNameToGlobalBusProxy);
        parmNameToOpenBeanSetterEntry = Collections.unmodifiableMap(parmNameToOpenBeanSetterEntry);
    }

    private void recordGlobalBuses(MFParmBusProxy busProxyBean) {
        Map<String, BusEntry> parmNameToEntry = busProxyBean.getParmNameToBusEntry();

        for (Map.Entry<String, BusEntry> mapEntry : parmNameToEntry.entrySet()) {
            BusEntry value = mapEntry.getValue();
            if (value.isGlobal() == false) {
                continue;
            }
            String parmName = mapEntry.getKey();
            putToGlobalBuses(busProxyBean, parmName);
        }

    }

    private void tryConnectToBusesAndRecordOpenSetters(Object bean) {
        Class<?> originClass = bean.getClass();
        while (Enhancer.isEnhanced(originClass)) {
            originClass = originClass.getSuperclass();
        }
        Map<String, Method> parmNameToSetter = MFParmUtils.searchParameterNameToSetter(originClass);
        for (Map.Entry<String, Method> mapEntry : parmNameToSetter.entrySet()) {
            String parmName = mapEntry.getKey();
            Method method = mapEntry.getValue();
            if (MFParmUtils.isLocal(method)) {
                continue;
            }
            MFParmBusProxy busProxy = parmNameToGlobalBusProxy.get(parmName);

            if (null == busProxy || busProxy == bean) {
                putToOpens(parmName, bean, method);
            } else {
                busProxy.registerToWeakBus(parmName, method, bean);
            }
        }
    }

    private void putToGlobalBuses(MFParmBusProxy busProxyBean, String parmName) {
        if (parmNameToGlobalBusProxy.containsKey(parmName)) {
            throw new IllegalStateException("conflicting global bus key " + parmName + ": " + busProxyBean + " vs. "
                    + parmNameToGlobalBusProxy.get(parmName));
        }
        parmNameToGlobalBusProxy.put(parmName, busProxyBean);
    }

    private void putToOpens(String parKey, Object bean, Method setter) {
        if (parmNameToOpenBeanSetterEntry.containsKey(parKey)) {
            BeanSetterEntry beanSetterEntry = parmNameToOpenBeanSetterEntry.get(parKey);
            throw new IllegalAccessError("conflicting open parm key " + parKey + ": {" + bean + ", " + setter
                    + "} vs. {" + beanSetterEntry.getBean() + ", " + beanSetterEntry.getSetter() + "}");
        }
        SimpBeanSetterEntry beanSetterEntry = new SimpBeanSetterEntry();
        beanSetterEntry.setBean(bean);
        beanSetterEntry.setSetter(setter);
        parmNameToOpenBeanSetterEntry.put(parKey, beanSetterEntry);
    }
}
