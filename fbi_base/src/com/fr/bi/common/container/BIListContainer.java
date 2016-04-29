package com.fr.bi.common.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Connery on 2016/1/12.
 */
public class BIListContainer<T> extends BICollectionContainer<T> {
    @Override
    protected Collection initCollection() {
        return new ArrayList<T>();
    }

    @Override
    protected Collection<T> getContainer() {
        return super.getContainer();
    }

    protected T getLastOne() throws IndexOutOfBoundsException {
        return ((List<T>) container).get(container.size() - 1);
    }

    protected T getFirstOne() throws IndexOutOfBoundsException {
        return ((List<T>) container).get(0);
    }
}