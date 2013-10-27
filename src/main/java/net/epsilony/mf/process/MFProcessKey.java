/*
 /* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.util.MFKey;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFProcessKey implements MFKey {

    MULTITHREADABLE(Integer.class),
    THREADS_NUM(Integer.class);

    private MFProcessKey(Class<?> valueType) {
        this.valueType = valueType;
    }

    private final Class<?> valueType;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }

}
