package com.fr.swift.segment.column;

import com.fr.swift.cube.io.location.IResourceLocation;

/**
 * @author pony
 * @date 2017/10/9
 * 表示segment中的一列数据
 */
public interface Column<T> {
    /**
     * 获取字典编码的列
     *
     * @return 字典编码的列
     */
    DictionaryEncodedColumn<T> getDictionaryEncodedColumn();

    /**
     * 获取位图索引
     *
     * @return 位图索引
     */
    BitmapIndexedColumn getBitmapIndex();

    /**
     * 获取基础类型明细值的列
     *
     * @return 基础类型明细值的列
     */
    DetailColumn<T> getDetailColumn();

    IResourceLocation getLocation();
}
