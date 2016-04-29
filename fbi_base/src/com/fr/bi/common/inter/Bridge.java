package com.fr.bi.common.inter;

/**
 * Created by 小灰灰 on 2014/10/10.
 */
public interface Bridge<T> {
    public T run(long userId);
}