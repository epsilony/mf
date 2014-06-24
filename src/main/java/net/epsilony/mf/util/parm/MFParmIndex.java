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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import net.epsilony.mf.util.parm.ann.AsSubBus;
import net.epsilony.mf.util.parm.ann.BusTrigger;
import net.epsilony.mf.util.parm.ann.MFParmOptional;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmIndex {

    private Map<String, MFParmDescriptor> parmDescriptors = new LinkedHashMap<>();

    public static MFParmIndex fromClass(Class<?> cls) {
        Class<?> oriCls = cls;
        while (Enhancer.isEnhanced(oriCls)) {
            oriCls = oriCls.getSuperclass();
        }

        Map<String, MFParmDescriptor> parmDiscriptors = new LinkedHashMap<>();
        Method[] methods = oriCls.getMethods();
        for (Method method : methods) {
            if (MFParmUtils.isSetter(method)) {
                String parmName = MFParmUtils.getParmName(method);
                MethodBaseMFParmDescriptor discriptor = new MethodBaseMFParmDescriptor();
                discriptor.setOriMethod(method);
                try {
                    discriptor.setMethod(cls == oriCls ? method : cls.getMethod(method.getName(),
                            method.getParameterTypes()));
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new IllegalStateException(e);
                }
                parmDiscriptors.put(parmName, discriptor);
            }
        }

        parmDiscriptors = Collections.unmodifiableMap(parmDiscriptors);

        MFParmIndex result = new MFParmIndex();
        result.setParmDescriptors(parmDiscriptors);
        return result;
    }

    public MFParmDescriptor getParmDescriptor(String parm) {
        return parmDescriptors.get(parm);
    }

    public Set<String> getParms() {
        return parmDescriptors.keySet();
    }

    public Map<String, MFParmDescriptor> getParmDescriptors() {
        return parmDescriptors;
    }

    public void setParmDescriptors(Map<String, MFParmDescriptor> parmDescriptors) {
        this.parmDescriptors = parmDescriptors;
    }

    public interface MFParmDescriptor {

        public abstract boolean isAsSubBus();

        public abstract String getName();

        public abstract String[] getTriggerAim();

        public abstract boolean isTrigger();

        public abstract boolean isOptional();

        BiConsumer<Object, Object> getObjectValueSetter();

        default void setObjectValue(Object obj, Object value) {
            getObjectValueSetter().accept(obj, value);
        }

    }

    public static class MethodBaseMFParmDescriptor implements MFParmDescriptor {
        private Method                     oriMethod;
        private Method                     method;
        private BiConsumer<Object, Object> objectValueSetter;
        {
            objectValueSetter = (obj, val) -> {
                try {
                    method.invoke(obj, val);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            };
        }

        public MethodBaseMFParmDescriptor() {

        }

        @Override
        public BiConsumer<Object, Object> getObjectValueSetter() {
            return objectValueSetter;
        }

        @Override
        public boolean isOptional() {
            return oriMethod.isAnnotationPresent(MFParmOptional.class);
        }

        @Override
        public boolean isTrigger() {
            return oriMethod.isAnnotationPresent(BusTrigger.class);
        }

        @Override
        public String[] getTriggerAim() {
            return MFParmUtils.getTriggerAims(oriMethod);
        }

        @Override
        public String getName() {
            return MFParmUtils.getParmName(oriMethod);
        }

        @Override
        public boolean isAsSubBus() {
            return oriMethod.isAnnotationPresent(AsSubBus.class);
        }

        public Method getOriMethod() {
            return oriMethod;
        }

        private void setOriMethod(Method oriMethod) {
            this.oriMethod = oriMethod;
        }

        public Method getMethod() {
            return method;
        }

        private void setMethod(Method method) {
            this.method = method;
        }

    }
}
