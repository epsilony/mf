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

import net.epsilony.mf.util.parm.ann.BusTrigger;
import net.epsilony.mf.util.parm.ann.AsSubBus;
import net.epsilony.mf.util.parm.ann.MFParmOptional;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmIndex {

    private Map<String, MFParmDescriptor> parmDiscriptors = new LinkedHashMap<>();

    public MFParmIndex(Class<?> cls) {
        Class<?> oriCls = cls;
        while (Enhancer.isEnhanced(oriCls)) {
            oriCls = oriCls.getSuperclass();
        }

        Method[] methods = oriCls.getMethods();
        for (Method method : methods) {
            if (MFParmUtils.isSetter(method)) {
                String parmName = MFParmUtils.getParmName(method);
                MFParmDescriptor discriptor = new MFParmDescriptor();
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
    }

    public MFParmDescriptor getParmDescriptor(String parm) {
        return parmDiscriptors.get(parm);
    }

    public Set<String> getParms() {
        return parmDiscriptors.keySet();
    }

    public Map<String, MFParmDescriptor> getParmDescriptors() {
        return parmDiscriptors;
    }

    public class MFParmDescriptor {
        private Method oriMethod;
        private Method method;

        private MFParmDescriptor() {

        }

        public boolean isOptional() {
            return oriMethod.isAnnotationPresent(MFParmOptional.class);
        }

        public boolean isTrigger() {
            return oriMethod.isAnnotationPresent(BusTrigger.class);
        }

        public String[] getTriggerAim() {
            return MFParmUtils.getTriggerAims(oriMethod);
        }

        public String getName() {
            return MFParmUtils.getParmName(oriMethod);
        }

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
