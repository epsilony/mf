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
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.epsilony.mf.util.parm.MFParmIndex.MFParmDescriptor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmContainerImplementor<T extends MFParmContainer> {

    private T                        proxied;
    private Class<T>                 cls;
    private TriggerParmToBusSwitcher switcher;
    private MFParmIndex              parmIndex;

    public static <T extends MFParmContainer> T newInstance(Class<T> cls) {
        return new MFParmContainerImplementor<T>(cls).getProxied();
    }

    public MFParmContainerImplementor(Class<T> cls) {
        setClass(cls);
        initParmIndex();
        initSwitcher();
        initProxied();
    }

    private void setClass(Class<T> cls) {
        if (Enhancer.isEnhanced(cls)) {
            throw new IllegalArgumentException("not supporting");
        }
        this.cls = cls;
    }

    public T getProxied() {
        return proxied;
    }

    public TriggerParmToBusSwitcher getSwitcher() {
        return switcher;
    }

    public MFParmIndex getParmIndex() {
        return parmIndex;
    }

    public void initProxied() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new InnerInterceptor());
        @SuppressWarnings("unchecked")
        T result = (T) enhancer.create();
        proxied = result;
    }

    void initParmIndex() {
        parmIndex = MFParmIndex.fromClass(cls);
    }

    void initSwitcher() {
        switcher = new TriggerParmToBusSwitcher();
        Map<String, MFParmDescriptor> parmDiscriptors = parmIndex.getParmDescriptors();
        for (Map.Entry<String, MFParmDescriptor> mapEntry : parmDiscriptors.entrySet()) {
            MFParmDescriptor descriptor = mapEntry.getValue();
            if (!descriptor.isTrigger()) {
                continue;
            }
            switcher.addTriggerParm(descriptor.getName(), descriptor.getTriggerAim());
        }

        Set<String> busNames = switcher.getBusNames();
        for (Method method : cls.getMethods()) {
            if (!MFParmUtils.isGetter(method)) {
                continue;
            }
            String busName = MFParmUtils.getParmName(method);
            if (!busNames.contains(busName)) {
                continue;
            }
            switcher.setBusGlobal(busName, MFParmUtils.isGlobal(method));
            switcher.setBusValueSource(busName, new MethodInvokeSupplier(method));
        }
    }

    private class MethodInvokeSupplier implements Supplier<Object> {
        private Method method;

        private MethodInvokeSupplier(Method method) {
            this.method = method;
        }

        @Override
        public Object get() {
            try {
                return method.invoke(proxied);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException();
            }
        }

    }

    private class InnerInterceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

            if (method.getDeclaringClass() == MFParmContainer.class) {
                if (method.getReturnType() == TriggerParmToBusSwitcher.class) {
                    return getSwitcher();
                } else if (method.getReturnType() == MFParmIndex.class) {
                    return getParmIndex();
                }
            }
            Object ret = proxy.invokeSuper(obj, args);
            if (MFParmUtils.isParmSetter(method)) {
                String parmName = MFParmUtils.getParmName(method);
                if (getSwitcher().getTriggerParms().contains(parmName)) {
                    getSwitcher().triggerParmAims(parmName);
                }
            }
            return ret;
        }
    }
}
