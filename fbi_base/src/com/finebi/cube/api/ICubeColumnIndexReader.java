package com.finebi.cube.api;


import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.Map.Entry;


public interface ICubeColumnIndexReader<T> {

    GroupValueIndex[] getGroupIndex(T[] groupValues);

    T firstKey();

    T lastKey();

    /**
     * entry的set
     *
     * @return entry的set
     */
    Iterator<Entry<T, GroupValueIndex>> iterator();

    /**
     * 反向的entry的set
     *
     * @return entry的set
     */
    Iterator<Entry<T, GroupValueIndex>> previousIterator();


    /**
     * 从某个值开始的entry的set
     *
     * @return entry的set
     */
    Iterator<Entry<T, GroupValueIndex>> iterator(T start);

    /**
     * 从某个值开始的反向的entry的set
     *
     * @return entry的set
     */
    Iterator<Entry<T, GroupValueIndex>> previousIterator(T start);

    /**
     * 非精确大小,仅用于日志时间估算
     *
     * @return
     */
    long nonPrecisionSize();

    /**
     * key数组
     *
     * @param length 长度
     * @return key 数组
     */
    T[] createKey(int length);


    T createValue(Object v);

}