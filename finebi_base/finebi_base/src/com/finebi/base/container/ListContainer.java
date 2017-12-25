package com.finebi.base.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class created on 2017/10/30
 *
 * @author Each.Zhang
 */
public class ListContainer<T> extends CollectionContainer<T> {

    @Override
    protected Collection<T> initCollection() {
        return new ArrayList<T>();
    }

    @Override
    protected List<T> getContainer() {
        return (List<T>)super.getContainer();
    }
}
