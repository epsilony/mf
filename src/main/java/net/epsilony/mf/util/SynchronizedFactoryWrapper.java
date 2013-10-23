/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

import net.epsilony.tb.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SynchronizedFactoryWrapper<T> implements Factory<T> {

    Factory<? extends T> factory;

    public SynchronizedFactoryWrapper(Factory<? extends T> factory) {
        this.factory = factory;
    }

    @Override
    synchronized public T produce() {
        return factory.produce();
    }
}
