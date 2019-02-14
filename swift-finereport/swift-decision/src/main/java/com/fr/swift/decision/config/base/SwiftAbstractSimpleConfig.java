package com.fr.swift.decision.config.base;

import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.Conf;

/**
 * @author yee
 * @date 2018/6/15
 */
public abstract class SwiftAbstractSimpleConfig<T> extends DefaultConfiguration implements SwiftSimpleConfig<T> {

    private Conf<T> configHolder;

    public SwiftAbstractSimpleConfig(Conf<T> configHolder) {
        this.configHolder = configHolder;
    }

    @Override
    public boolean addOrUpdate(final T obj) {
        configHolder.set(obj);
        return true;
    }

    @Override
    public T get() {
        return configHolder.get();
    }

    @Override
    public boolean remove() {
        return false;
    }

    public abstract String getNameSpace();
}
