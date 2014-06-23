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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.util.parm.ann.AsSubBus;
import net.epsilony.mf.util.parm.ann.BusTrigger;
import net.epsilony.mf.util.parm.ann.GlobalBus;
import net.epsilony.mf.util.parm.ann.MFParmIgnore;
import net.epsilony.mf.util.parm.ann.MFParmName;
import net.epsilony.mf.util.parm.ann.MFParmNullPolicy;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmUtils {

    public static boolean isNullPermitted(Class<?> cls) {
        MFParmNullPolicy nullPolicy = cls.getAnnotation(MFParmNullPolicy.class);
        return null != nullPolicy && nullPolicy.permit() == true;
    }

    public static boolean isNullPermitted(boolean defaultPermit, Method method) {
        MFParmNullPolicy nullPolicy = method.getAnnotation(MFParmNullPolicy.class);
        boolean permitNull = null != nullPolicy ? nullPolicy.permit() : defaultPermit;
        return permitNull;
    }

    public static Map<String, Method> searchParameterNameToSetter(Class<?> cls) {
        Map<String, Method> result = new HashMap<>();
        for (Method method : cls.getMethods()) {
            if (isParmSetter(method) == false) {
                continue;
            }
            String parameterName = getParmName(method);
            if (result.containsKey(parameterName)) {
                throw new IllegalStateException("conflicting parm setter: " + method + " vs. "
                        + result.get(parameterName));
            }
            result.put(parameterName, method);
        }
        return result;
    }

    public static Map<String, Method> searchParameterNameToGetter(Class<?> cls) {

        Map<String, Method> result = new HashMap<>();

        for (Method method : cls.getMethods()) {
            if (isParmGetter(method) == false) {
                continue;
            }
            String parameterName = getParmName(method);
            result.put(parameterName, method);
        }
        return result;
    }

    public static String getParmName(Method method) {
        if (method.isAnnotationPresent(MFParmIgnore.class)) {
            return null;
        }
        MFParmName effectiveName = method.getAnnotation(MFParmName.class);
        if (null == effectiveName) {
            return getPropertyName(method);
        } else {
            return effectiveName.value();
        }
    }

    public static String getPropertyName(Method method) {
        String prefix = null;
        String name = method.getName();
        if (isGetter(method)) {
            prefix = "get";
            if (name.startsWith("is")) {
                prefix = "is";
            }
        } else if (isSetter(method)) {
            prefix = "set";
        } else {
            return null;
        }

        return Character.toLowerCase(name.charAt(prefix.length())) + name.substring(prefix.length() + 1);
    }

    public static boolean isParmSetter(Method method) {
        if (method.isAnnotationPresent(MFParmIgnore.class)) {
            return false;
        }
        return isSetter(method);
    }

    public static boolean isSetter(Method method) {

        if (Modifier.isPublic(method.getModifiers()) == false) {
            return false;
        }
        if (method.getReturnType() != void.class || method.getParameterCount() != 1) {
            return false;
        }
        String name = method.getName();
        if (!name.startsWith("set") || !Character.isUpperCase(name.charAt(3))) {
            return false;
        }
        return true;
    }

    public static boolean isParmGetter(Method method) {
        if (method.isAnnotationPresent(MFParmIgnore.class)) {
            return false;
        }
        return isGetter(method);
    }

    public static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) == false) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class || method.getParameterCount() != 0) {
            return false;
        }

        String name = method.getName();
        String prefix = "get";
        if (!name.startsWith(prefix)) {
            if (boolean.class.equals(returnType) || Boolean.class.equals(returnType)) {
                prefix = "is";
            } else {
                return false;
            }
        }
        if (!Character.isUpperCase(name.charAt(prefix.length()))) {
            return false;
        }
        return true;

    }

    public static boolean isLocal(Method method) {
        return !isGlobal(method);
    }

    public static boolean isGlobal(Method method) {
        GlobalBus annotation = method.getAnnotation(GlobalBus.class);
        if (annotation == null) {
            return false;
        }
        return annotation.value();
    }

    public static String[] getTriggerAims(Method method) {
        boolean isSetter = isSetter(method);
        if (isSetter == false) {
            throw new IllegalArgumentException("@" + BusTrigger.class.getSimpleName() + "must be on a bean setter ("
                    + method + ")");
        }
        BusTrigger busTrigger = method.getAnnotation(BusTrigger.class);

        if (null == busTrigger) {
            throw new IllegalArgumentException("not @" + BusTrigger.class.getSimpleName() + " annotated");
        }
        String[] aims = busTrigger.aims();

        if (aims.length == 0) {

            String parameterName = getParmName(method);
            if (null == parameterName) {
                throw new IllegalStateException();
            }
            aims = new String[] { parameterName };
        }
        return aims;
    }

    public static void checkMethod(Method method) {
        boolean isParmSetter = isParmSetter(method);
        boolean isParmGetter = isParmGetter(method);
        if (!isParmGetter && !isParmSetter) {
            @SuppressWarnings("rawtypes")
            final Class[] notAllows = { AsSubBus.class, MFParmName.class, GlobalBus.class, BusTrigger.class };
            checkInvalidAnnotations(method, notAllows);
        } else if (isParmGetter) {
            @SuppressWarnings("rawtypes")
            final Class[] notAllows = { BusTrigger.class, };
            checkInvalidAnnotations(method, notAllows);
        } else if (isParmSetter) {
            @SuppressWarnings("rawtypes")
            final Class[] notAllows = {};
            checkInvalidAnnotations(method, notAllows);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void checkInvalidAnnotations(Method method, final Class[] notAllows) {
        for (Class cls : notAllows) {
            if (method.isAnnotationPresent(cls)) {
                throw new IllegalArgumentException("@" + cls.getSimpleName() + " is not allowed for " + method);
            }
        }
    }
}
