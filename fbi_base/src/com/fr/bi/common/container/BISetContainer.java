package com.fr.bi.common.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connery on 2015/12/28.
 */
public class BISetContainer<T> extends BICollectionContainer<T> {
    @Override
    protected Collection initCollection() {
        return new HashSet<T>();
    }

    @Override
    protected Set<T> getContainer() {
        return (Set<T>) super.getContainer();
    }

}