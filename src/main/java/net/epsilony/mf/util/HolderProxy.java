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
package net.epsilony.mf.util;

import java.lang.reflect.Proxy;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.PhysicalModel;
import net.epsilony.mf.model.RawPhysicalModel;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class HolderProxy<T> {

    Class<T> type;

    HolderProxyInvocationHandler<T> proxyInvocationHandler = new HolderProxyInvocationHandler<>();

    T proxied;

    public HolderProxy() {
    }

    public HolderProxy(Class<T> type) {
        setType(type);
    }

    public Class<? extends T> getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public void setType(Class<T> type) {
        this.type = type;
        proxied = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, proxyInvocationHandler);
    }

    public T getProxied() {
        return proxied;
    }

    public void setReal(T t) {
        proxyInvocationHandler.setReal(t);
    }

    public T getReal() {
        return proxyInvocationHandler.getReal();
    }

    public static void main(String[] args) {
        HolderProxy<PhysicalModel> holderProxy = new HolderProxy<>(PhysicalModel.class);
        RawPhysicalModel rawRhysModel = new RawPhysicalModel();
        rawRhysModel.setGeomRoot(new MFNode(new double[] { 0, 2 }));
        PhysicalModel proxied2 = holderProxy.getProxied();
        holderProxy.setReal(rawRhysModel);
        GeomUnit geomRoot = proxied2.getGeomRoot();
        System.out.println("geomRoot = " + geomRoot);
    }
}
