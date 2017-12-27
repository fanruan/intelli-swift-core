package com.finebi.base.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2017/9/30.
 *
 * @author Each.Zhang
 */
public class SetContainer<T> extends CollectionContainer<T> {


    @Override
    protected Collection<T> initCollection() {
        return new HashSet<T>();
    }

    /**
     * Collection强转成具体的类型
     * @return
     */
    @Override
    protected Set<T> getContainer() {
        return (Set<T>)super.getContainer();
    }
}
