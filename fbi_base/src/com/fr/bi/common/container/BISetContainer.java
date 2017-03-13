package com.fr.bi.common.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connery on 2015/12/28.
 */
public class BISetContainer<T> extends BICollectionContainer<T> {
    private static final long serialVersionUID = 6609514741028577818L;

    @Override
    protected Collection initCollection() {
        return new HashSet<T>();
    }

    @Override
    protected Set<T> getContainer() {
        return (Set<T>) super.getContainer();
    }

}