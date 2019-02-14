package com.fr.swift.segment.column;

import com.fr.swift.cube.io.Flushable;
import com.fr.swift.cube.io.IfReadable;
import com.fr.swift.cube.io.Releasable;

/**
 * @author pony
 * @date 2017/10/9
 * <p>
 * 获取基础类型的列，用于聚合
 */
public interface DetailColumn<T> extends Releasable, Flushable, IfReadable {
    int getInt(int pos);

    long getLong(int pos);

    double getDouble(int pos);

    void put(int pos, T val);

    T get(int pos);
}