package com.fr.swift.cloud.segment.column;

import com.fr.swift.cloud.cube.io.IfReadable;
import com.fr.swift.cloud.cube.io.Releasable;

/**
 * @author pony
 * @date 2017/10/9
 * <p>
 * 获取基础类型的列，用于聚合
 */
public interface DetailColumn<T> extends Releasable, IfReadable {
    /**
     * @deprecated 不硬核，还是要根据类型决定getInt/Long/Double
     * 还不如强转成实现类，调实现类提供的具体方法
     */
    @Deprecated
    int getInt(int pos);

    /**
     * @deprecated 不硬核，还是要根据类型决定getInt/Long/Double
     * 还不如强转成实现类，调实现类提供的具体方法
     */
    @Deprecated
    long getLong(int pos);

    /**
     * @deprecated 不硬核，还是要根据类型决定getInt/Long/Double
     * 还不如强转成实现类，调实现类提供的具体方法
     */
    @Deprecated
    double getDouble(int pos);

    void put(int pos, T val);

    T get(int pos);
}