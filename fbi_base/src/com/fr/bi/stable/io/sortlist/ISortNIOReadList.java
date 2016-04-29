package com.fr.bi.stable.io.sortlist;


import com.fr.bi.stable.io.newio.NIOReader;

/**
 * Created by GUY on 2015/3/18.
 */
public interface ISortNIOReadList<T> extends NIOReader<T> {
    int[] indexOf(T[] values);

    int size();

    T[] createKey(int length);

    T createValue(Object v);

}