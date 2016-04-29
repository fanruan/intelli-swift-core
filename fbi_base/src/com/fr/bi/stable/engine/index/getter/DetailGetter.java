package com.fr.bi.stable.engine.index.getter;

public interface DetailGetter<T> {

    T getValueObject(int row);

    Object getValue(int row);

}