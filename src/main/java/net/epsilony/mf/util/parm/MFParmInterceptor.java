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

import net.epsilony.mf.util.parm.MFParmBusProxy.MFParmBusProxyImp;
import net.epsilony.mf.util.parm.ann.MFParmBusTrigger;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MFParmInterceptor implements MethodInterceptor {

    public static Logger           logger = LoggerFactory.getLogger(MFParmInterceptor.class);
    private final MFParmBusProxyImp parmBusProxy;
    private final Object           target;
    private final Object           proxied;

    public MFParmInterceptor(Object target) {
        this.target = target;
        parmBusProxy = new MFParmBusProxy.MFParmBusProxyImp(target);
        proxied = generateProxied();

    }

    private Object generateProxied() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        enhancer.setInterfaces(new Class[] { MFParmBusProxy.class, MFParmMonitor.class });

        Object _proxied = enhancer.create();
        return _proxied;
    }

    public Object getTarget() {
        return target;
    }

    public Object getProxied() {
        return proxied;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        Object ret = null;
        if (method.getDeclaringClass().equals(MFParmBusProxy.class)) {
            ret = method.invoke(parmBusProxy, args);
        } else {
            if (Modifier.isAbstract(method.getModifiers())) {
                ret = null;
            } else {
                ret = method.invoke(target, args);
            }
        }

        if (method.isAnnotationPresent(MFParmBusTrigger.class)) {
            parmBusProxy.triggerMethodInvoked(method);
        }

        return ret;
    }
}
