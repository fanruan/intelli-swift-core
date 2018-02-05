package com.fr.swift.segment.column;

import com.fr.swift.cube.io.Flushable;
import com.fr.swift.cube.io.Releasable;

import java.util.Comparator;

/**
 * @author pony
 * @date 2017/10/9
 * 字典编码的列
 */
public interface DictionaryEncodedColumn<T> extends Releasable, Flushable {
    String KEY = "dict_key";

    String INDEX = "dict_index";
    String SIZE = "dict_size";

    String GLOBAL_INDEX = "global_dict_index";
    String GLOBAL_SIZE = "global_dict_size";

    /**
     * 写入字典的长度，唯一值的个数
     *
     * @param size 字典的长度
     */
    void putSize(int size);

    int size();

    /**
     * 写入全局字典的长度，全局唯一值的个数
     *
     * @return 全局字典的长度
     */
    void putGlobalSize(int globalSize);

    int globalSize();

    /**
     * 对应位置写入字典值
     * 序号 -> 值
     *
     * @param index 字典序号
     * @param val   值
     */
    void putValue(int index, T val);

    T getValue(int index);

    /**
     * 获取值对应的字典序号
     *
     * @param value 字典值
     * @return 对应序号
     */
    int getIndex(Object value);

    /**
     * 行号 -> 字典序号
     *
     * @param row   行号
     * @param index 字典序号
     */
    void putIndex(int row, int index);

    int getIndexByRow(int row);

    /**
     * 字典序号 -> 全局字典序号
     *
     * @param index       序号
     * @param globalIndex 全局序号
     */
    void putGlobalIndex(int index, int globalIndex);

    int getGlobalIndexByIndex(int index);

    /**
     * 行号 -> 全局字典序号
     * 便利方法，相当于getGlobalIndexByIndex(getIndexByRow(row))
     *
     * @param row 行号
     * @return 对应序号
     */
    int getGlobalIndexByRow(int row);

    /**
     * 获取字典编码列排序的比较器
     *
     * @return 比较器
     */
    Comparator<T> getComparator();
}